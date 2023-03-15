package com.rendersoncs.report.view.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.rendersoncs.report.R
import com.rendersoncs.report.databinding.FragmentRemoveUserBinding
import com.rendersoncs.report.infrastructure.util.closeVirtualKeyBoard
import com.rendersoncs.report.view.viewmodel.ReportViewModel
import com.rendersoncs.report.view.base.BaseFragment
import com.rendersoncs.report.view.login.util.User

class RemoveUserFragment : BaseFragment<FragmentRemoveUserBinding, ReportViewModel>(),
        DatabaseReference.CompletionListener,
        TextView.OnEditorActionListener {

    override val viewModel: ReportViewModel by activityViewModels()
    private lateinit var user: User
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        mAuth = FirebaseAuth.getInstance()
    }

    override fun onResume() {
        super.onResume()
        init()
    }

    private fun init() = with(binding) {
        user = User()
        user.id = mAuth.currentUser!!.uid
        user.email = mAuth.currentUser!!.email

        this.password.setOnEditorActionListener(this@RemoveUserFragment)
        this.password.addTextChangedListener(passwordWatcher)

        this.buttonRemoved.setOnClickListener {
            removeUser()
        }
    }

    private val passwordWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(s: Editable?) {
            validatePassword(s)
        }
    }

    private fun validatePassword(s: Editable?) {
        if (s != null && s.isNotEmpty()) {
            binding.textInputDeleteAccount.error = null
        } else {
            binding.textInputDeleteAccount.error = getString(R.string.label_insert_password)
        }
    }

    private fun removeUser() {
        if (binding.password.text.toString().isEmpty()) {
            binding.textInputDeleteAccount.error = getString(R.string.label_insert_password)
        } else {
            user.password = binding.password.text.toString()
            reAuthenticate()
        }
    }

    private fun reAuthenticate() {
        val firebaseUser = mAuth.currentUser ?: return
        val credential = user.email?.let { email ->
            user.password?.let { password ->
                EmailAuthProvider.getCredential(
                        email,
                        password
                )
            }
        }
        firebaseUser.reauthenticate(credential!!)
                .addOnCompleteListener { task: Task<Void?> ->
                    if (task.isSuccessful) {
                        deleteUser()
                    }
                }.addOnFailureListener { e: Exception ->
                    FirebaseCrashlytics.getInstance().recordException(e)
                    toast(e.message.toString())
                }
    }

    private fun deleteUser() {
        val firebaseUser = mAuth.currentUser ?: return
        firebaseUser.delete().addOnCompleteListener { task: Task<Void?> ->
            if (!task.isSuccessful) {
                return@addOnCompleteListener
            }
            user.removeDB(this@RemoveUserFragment)
        }
                .addOnFailureListener { e: Exception ->
                    FirebaseCrashlytics.getInstance().recordException(e)
                    toast(e.message.toString())
                }
    }

    override fun onComplete(databaseError: DatabaseError?, databaseReference: DatabaseReference) {
        if (databaseError != null) {
            FirebaseCrashlytics.getInstance().recordException(databaseError.toException())
        }
        toast(getString(R.string.label_account_removed))
        findNavController().navigate(
                R.id.action_removeUser_to_login
        )
    }

    override fun onEditorAction(view: TextView, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            closeVirtualKeyBoard(
                    requireContext(),
                    view
            )
            this.removeUser()
            return true
        }
        return false
    }

    override fun getViewBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ) = FragmentRemoveUserBinding.inflate(inflater, container, false)
}