package com.rendersoncs.report.view.login

import android.content.Intent
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
import com.google.firebase.auth.FirebaseAuth
import com.rendersoncs.report.R
import com.rendersoncs.report.databinding.FragmentRecoveryLoginBinding
import com.rendersoncs.report.common.util.closeVirtualKeyBoard

class RecoveryLoginActivity : AppCompatActivity(), TextView.OnEditorActionListener {
    private var firebaseAuth: FirebaseAuth? = null
    private lateinit var binding: FragmentRecoveryLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentRecoveryLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.emailRecover.setOnEditorActionListener(this)
        binding.emailRecover.addTextChangedListener(emailTextWatcher)
        binding.sendHome.setOnClickListener { callLoginActivity() }
    }

    private fun callLoginActivity() {
        finish()
    }

    private val emailTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(s: Editable?) {
            validatePassword(s)
        }
    }

    private fun validatePassword(s: Editable?) {
        if (s != null && s.isNotEmpty()) {
            binding.textInputEmailRecover.error = null
        } else {
            binding.textInputEmailRecover.error = getString(R.string.label_insert_email)
        }
    }

    fun btnRecoverAccount(view: View?) {
        recoveryAccount()
    }

    private fun recoveryAccount() {
        if (binding.emailRecover.text.toString().isEmpty()) {
            binding.textInputEmailRecover.error = getString(R.string.label_insert_email)
        } else {
            firebaseAuth
                    ?.sendPasswordResetEmail(binding.emailRecover.text.toString())
                    ?.addOnCompleteListener { task: Task<Void?> ->
                        if (task.isSuccessful) {
                            binding.emailRecover.setText("")
                            Toast.makeText(
                                    this@RecoveryLoginActivity,
                                    resources.getString(R.string.label_recover_access_send),
                                    Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                    this@RecoveryLoginActivity,
                                    resources.getString(R.string.label_failed),
                                    Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    ?.addOnFailureListener { obj: Exception -> obj.printStackTrace() }
        }
    }

    override fun onEditorAction(view: TextView?, actionId: Int, keyEvent: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            closeVirtualKeyBoard(
                    this,
                    view!!
            )
            this.recoveryAccount()
            return true
        }
        return false
    }
}