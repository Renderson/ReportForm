package com.rendersoncs.report.view.login.loginV2

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.rendersoncs.report.R
import com.rendersoncs.report.databinding.FragmentRecoveryLoginBinding
import com.rendersoncs.report.infrastructure.util.closeVirtualKeyBoard
import com.rendersoncs.report.view.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecoveryLoginFragment : BaseFragment<FragmentRecoveryLoginBinding, LoginViewModel>(),
    TextView.OnEditorActionListener {
    override val viewModel: LoginViewModel by activityViewModels()

    private var firebaseAuth = FirebaseAuth.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    private fun initViews() = with(binding) {
        emailRecover.setOnEditorActionListener(this@RecoveryLoginFragment)
        emailRecover.addTextChangedListener(emailTextWatcher)

        sendHome.setOnClickListener {
            findNavController().navigate(
                R.id.action_recovery_to_homeLogin
            )
        }

        recoveryAccount.setOnClickListener {
            recoveryAccount()
        }
    }

    private val emailTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(s: Editable?) {
            validateEmail(s)
        }
    }

    private fun validateEmail(s: Editable?) = with(binding) {
        if (s != null && s.isNotEmpty()) {
            textInputEmailRecover.error = null
        } else {
            textInputEmailRecover.error = getString(R.string.label_insert_email)
        }
    }

    private fun recoveryAccount() = with(binding) {
        if (emailRecover.text.toString().isEmpty()) {
            textInputEmailRecover.error = getString(R.string.label_insert_email)
        } else {
            firebaseAuth
                .sendPasswordResetEmail(emailRecover.text.toString())
                .addOnCompleteListener { task: Task<Void?> ->
                    if (task.isSuccessful) {
                        emailRecover.setText("")
                        toast(getString(R.string.label_recover_access_send))
                    } else {
                        toast(getString(R.string.label_failed))
                    }
                }
                .addOnFailureListener { obj: Exception -> obj.printStackTrace() }
        }
    }

    override fun onEditorAction(view: TextView?, actionId: Int, keyEvent: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            closeVirtualKeyBoard(
                requireContext(),
                requireView()
            )
            this.recoveryAccount()
            return true
        }
        return false
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentRecoveryLoginBinding.inflate(inflater, container, false)
}