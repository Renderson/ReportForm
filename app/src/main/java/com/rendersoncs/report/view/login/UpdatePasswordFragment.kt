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
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.rendersoncs.report.R
import com.rendersoncs.report.databinding.FragmentUpdatePasswordBinding
import com.rendersoncs.report.common.util.closeVirtualKeyBoard
import com.rendersoncs.report.view.viewmodel.ReportViewModel
import com.rendersoncs.report.view.base.BaseFragment
import com.rendersoncs.report.view.login.util.User
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdatePasswordFragment : BaseFragment<FragmentUpdatePasswordBinding, ReportViewModel>(), TextView.OnEditorActionListener {
    override val viewModel: ReportViewModel by activityViewModels()
    private lateinit var user: User
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        mAuth = FirebaseAuth.getInstance()
    }

    override fun getViewBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ) = FragmentUpdatePasswordBinding.inflate(inflater, container, false)

    private fun init() = with(binding) {
        user = User()
        user.id = mAuth.currentUser!!.uid
        user.email = mAuth.currentUser!!.email

        this.oldPassword.setOnEditorActionListener(this@UpdatePasswordFragment)
        this.oldPassword.addTextChangedListener(oldPasswordWatcher)
        this.newPassword.addTextChangedListener(newPasswordWatcher)

        this.btnUpdate.setOnClickListener {
            update()
        }
    }

    private val oldPasswordWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(s: Editable?) {
            validatePassword(s, binding.textInputOldPassword)
        }
    }

    private val newPasswordWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(s: Editable?) {
            validatePassword(s, binding.textInputNewPassword)
        }
    }

    private fun validatePassword(s: Editable?, inputText: TextInputLayout) {
        if (s != null && s.isNotEmpty()) {
            inputText.error = null
        } else {
            inputText.error = getString(R.string.label_insert_password)
        }
    }

    private fun update() = with(binding) {
        when {
            this.newPassword.text.toString().isEmpty() -> {
                this.textInputNewPassword.error = getString(R.string.label_insert_password)
            }
            this.oldPassword.text.toString().isEmpty() -> {
                this.textInputOldPassword.error = getString(R.string.label_insert_password)
            }
            else -> {
                user.newPassword = this.newPassword.text.toString()
                user.password = this.oldPassword.text.toString()
                reAuthenticate()
            }
        }
    }

    private fun reAuthenticate() {
        val firebaseUser = mAuth.currentUser ?: return
        val credential = EmailAuthProvider.getCredential(
                user.email,
                user.password
        )
        firebaseUser.reauthenticate(credential)
                .addOnCompleteListener { task: Task<Void?> ->
                    if (task.isSuccessful) {
                        updateData()
                    }
                }
                .addOnFailureListener { e: Exception ->
                    FirebaseCrashlytics.getInstance().recordException(e)
                    toast(e.message.toString())
                }
    }

    private fun updateData() {
        user.newPassword = binding.newPassword.text.toString()
        user.password = binding.oldPassword.text.toString()
        val firebaseUser = mAuth.currentUser ?: return
        firebaseUser
                .updatePassword(user.newPassword)
                .addOnCompleteListener { task: Task<Void?> ->
                    if (task.isSuccessful) {
                        binding.newPassword.setText("")
                        binding.oldPassword.setText("")
                        toast(getString(R.string.label_password_update))
                    }
                }
                .addOnFailureListener(requireActivity()) { e: Exception ->
                    FirebaseCrashlytics.getInstance().recordException(e)
                    toast(e.message.toString())
                }
    }

    override fun onEditorAction(view: TextView, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            closeVirtualKeyBoard(
                    requireContext(),
                    view
            )
            this.update()
            return true
        }
        return false
    }

    override fun onResume() {
        super.onResume()
        this.init()
    }
}