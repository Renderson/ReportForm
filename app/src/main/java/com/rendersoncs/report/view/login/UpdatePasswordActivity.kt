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
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.rendersoncs.report.R
import com.rendersoncs.report.databinding.ActivityUpdatePasswordBinding
import com.rendersoncs.report.infrastructure.util.closeVirtualKeyBoard
import com.rendersoncs.report.view.login.util.User

class UpdatePasswordActivity : AppCompatActivity(), ValueEventListener, TextView.OnEditorActionListener {
    private var user: User? = null
    private var mAuth: FirebaseAuth? = null
    private lateinit var binding: ActivityUpdatePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUpdatePasswordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        mAuth = FirebaseAuth.getInstance()

        binding.oldPassword.setOnEditorActionListener(this)
        binding.oldPassword.addTextChangedListener(oldPasswordWatcher)
        binding.newPassword.addTextChangedListener(newPasswordWatcher)
    }

    private fun init() {
        user = User()
        user!!.id = mAuth!!.currentUser!!.uid
        user!!.contextDataDB(this)
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

    fun btnUpdate(view: View?) {
        this.update()
    }

    private fun update() {
        when {
            binding.newPassword.text.toString().isEmpty() -> {
                binding.textInputNewPassword.error = getString(R.string.label_insert_password)
            }
            binding.oldPassword.text.toString().isEmpty() -> {
                binding.textInputOldPassword.error = getString(R.string.label_insert_password)
            }
            else -> {
                user!!.newPassword = binding.newPassword.text.toString()
                user!!.password = binding.oldPassword.text.toString()
                reAuthenticate()
            }
        }
    }

    private fun reAuthenticate() {
        val firebaseUser = mAuth!!.currentUser ?: return
        val credential = EmailAuthProvider.getCredential(
                user!!.email,
                user!!.password
        )
        firebaseUser.reauthenticate(credential)
                .addOnCompleteListener { task: Task<Void?> ->
                    if (task.isSuccessful) {
                        updateData()
                    }
                }
                .addOnFailureListener { e: Exception ->
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Toast.makeText(
                            this@UpdatePasswordActivity,
                            e.message,
                            Toast.LENGTH_SHORT
                    ).show()
                }
    }

    private fun updateData() {
        user!!.newPassword = binding.newPassword.text.toString()
        user!!.password = binding.oldPassword.text.toString()
        val firebaseUser = mAuth!!.currentUser ?: return
        firebaseUser
                .updatePassword(user!!.newPassword)
                .addOnCompleteListener { task: Task<Void?> ->
                    if (task.isSuccessful) {
                        binding.newPassword.setText("")
                        binding.oldPassword.setText("")
                        Toast.makeText(
                                this@UpdatePasswordActivity,
                                resources.getString(R.string.label_password_update),
                                Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .addOnFailureListener(this) { e: Exception ->
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Toast.makeText(
                            this@UpdatePasswordActivity,
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

    override fun onEditorAction(view: TextView, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            closeVirtualKeyBoard(
                    this,
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

        binding.backButtonUpdate.setOnClickListener {
            finish()
        }
    }
}