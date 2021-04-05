package com.rendersoncs.report.view.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.rendersoncs.report.R
import com.rendersoncs.report.databinding.ActivityRemoveUserBinding
import com.rendersoncs.report.infrastructure.util.closeVirtualKeyBoard
import com.rendersoncs.report.view.login.util.User

class RemoveUserActivity : AppCompatActivity(), ValueEventListener,
        DatabaseReference.CompletionListener,
        TextView.OnEditorActionListener {

    private var user: User? = null
    private var mAuth: FirebaseAuth? = null
    private lateinit var binding: ActivityRemoveUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRemoveUserBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        mAuth = FirebaseAuth.getInstance()

        binding.password.setOnEditorActionListener(this)
        binding.password.addTextChangedListener(passwordWatcher)
    }

    override fun onResume() {
        super.onResume()
        init()

        binding.backButtonRemove.setOnClickListener {
            finish()
        }
    }

    private fun init() {
        user = User()
        user!!.id = mAuth!!.currentUser!!.uid
        user!!.contextDataDB(this)
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

    fun btnRemoveUser(view: View?) {
        this.removeUser()
    }

    private fun removeUser() {
        if (binding.password.text.toString().isEmpty()) {
            binding.textInputDeleteAccount.error = getString(R.string.label_insert_password)
        } else {
            user!!.password = binding.password.text.toString()
            reAuthenticate()
        }
    }

    private fun reAuthenticate() {
        val firebaseUser = mAuth!!.currentUser ?: return
        val credential = user!!.email?.let {
            user!!.password?.let { it1 ->
                EmailAuthProvider.getCredential(
                        it,
                        it1
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
                    Toast.makeText(
                            this@RemoveUserActivity,
                            e.message,
                            Toast.LENGTH_SHORT
                    ).show()
                }
    }

    private fun deleteUser() {
        val firebaseUser = mAuth!!.currentUser ?: return
        firebaseUser.delete().addOnCompleteListener { task: Task<Void?> ->
                    if (!task.isSuccessful) {
                        return@addOnCompleteListener
                    }
                    user!!.removeDB(this@RemoveUserActivity)
                }
                .addOnFailureListener { e: Exception ->
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Toast.makeText(
                            this@RemoveUserActivity,
                            e.message,
                            Toast.LENGTH_SHORT
                    ).show()
                }
    }

    override fun onDataChange(dataSnapshot: DataSnapshot) {
        val u = dataSnapshot.getValue(User::class.java)
        if (u != null) {
            user!!.email = u.email
        }
    }

    override fun onCancelled(fireBaseError: DatabaseError) {
        FirebaseCrashlytics.getInstance().recordException(fireBaseError.toException())
    }

    override fun onComplete(databaseError: DatabaseError?, databaseReference: DatabaseReference) {
        if (databaseError != null) {
            FirebaseCrashlytics.getInstance().recordException(databaseError.toException())
        }
        Toast.makeText(
                this@RemoveUserActivity,
                resources.getString(R.string.label_account_removed),
                Toast.LENGTH_SHORT
        ).show()
        finish()
    }

    override fun onEditorAction(view: TextView, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            closeVirtualKeyBoard(
                    this,
                    view
            )
            this.removeUser()
            return true
        }
        return false
    }
}