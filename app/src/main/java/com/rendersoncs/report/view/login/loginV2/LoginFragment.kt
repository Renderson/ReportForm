package com.rendersoncs.report.view.login.loginV2

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.*
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.rendersoncs.report.R
import com.rendersoncs.report.databinding.FragmentLoginBinding
import com.rendersoncs.report.infrastructure.util.afterTextChanged
import com.rendersoncs.report.infrastructure.util.closeVirtualKeyBoard
import com.rendersoncs.report.infrastructure.util.isValidateEmail
import com.rendersoncs.report.view.base.BaseBindingFragment
import com.rendersoncs.report.view.base.BaseFragment
import com.rendersoncs.report.view.login.util.User
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_login.*

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel>(),
    TextView.OnEditorActionListener {

    override val viewModel: LoginViewModel by activityViewModels()

    private var mAuth: FirebaseAuth? = null
    private var mAuthListener: FirebaseAuth.AuthStateListener? = null
    private var user: User? = null
    private var callbackManager: CallbackManager? = null
    private var mGoogleApiClient: GoogleSignInClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        mAuth = FirebaseAuth.getInstance()
        mAuthListener = firebaseAuthResultHandler
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    activity?.finish()
                }
            })

        /*FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        mAuth = FirebaseAuth.getInstance()
        mAuthListener = firebaseAuthResultHandler*/

        facebookSignIn()
        googleSignIn()
        initViews()
        initUser()
    }

    private fun facebookSignIn() = with(binding) {
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                accessFacebookLoginData(loginResult.accessToken)
            }

            override fun onCancel() {}
            override fun onError(error: FacebookException) {
                FirebaseCrashlytics.getInstance().recordException(error)
                closeProgressBar()
                showSnackBar(resources.getString(R.string.label_login_facebook_failed), loginProgress)
            }
        })
    }

    private fun accessFacebookLoginData(accessToken: AccessToken?) {
        accessLoginData(
            FACEBOOK,
            accessToken?.token
        )
    }

    private fun accessLoginData(provider: String, vararg tokens: String?) = with(binding) {
        if (tokens.isNotEmpty() && tokens[0] != null) {
            var credential: AuthCredential? = FacebookAuthProvider.getCredential(tokens[0]!!)
            credential =
                if (provider.equals(GOOGLE, ignoreCase = true)) GoogleAuthProvider.getCredential(
                    tokens[0],
                    null
                ) else credential
            credential = if (provider.equals(
                    "twitter",
                    ignoreCase = true
                )
            ) TwitterAuthProvider.getCredential(tokens[0]!!, tokens[1]!!) else credential

            user!!.saveProviderSP(requireContext(), provider)
            mAuth!!.signInWithCredential(credential!!)
                .addOnCompleteListener { task: Task<AuthResult?> ->
                    if (!task.isSuccessful) {
                        showSnackBar(resources.getString(R.string.label_login_social_failed), loginProgress)
                    }
                }
                .addOnFailureListener { obj: Exception -> obj.printStackTrace() }
        } else {
            mAuth!!.signOut()
        }
    }

    private fun googleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(GOOGLE_TOKEN)
            .requestEmail()
            .build()
        mGoogleApiClient = GoogleSignIn.getClient(requireActivity(), gso)
    }

    private val firebaseAuthResultHandler: FirebaseAuth.AuthStateListener
        get() = FirebaseAuth.AuthStateListener { firebaseAuth: FirebaseAuth ->
            val userFirebase = firebaseAuth.currentUser ?: return@AuthStateListener
            if (user!!.id == null
                && isNameOk(user, userFirebase)
            ) {
                user!!.id = userFirebase.uid
                user!!.setNameIfNull(userFirebase.displayName)
                user!!.setEmailIfNull(userFirebase.email)
                user!!.saveDB()
            }
            callMainActivity()
        }

    private fun isNameOk(user: User?, firebaseUser: FirebaseUser): Boolean {
        return (user!!.name != null
                || firebaseUser.displayName != null)
    }

    private fun callMainActivity() {
        activity?.findNavController(R.id.nav_host_fragment_login)
            ?.navigate(R.id.action_loginFragment_to_mainActivity)
        activity?.finish()
    }

    private fun initViews() = with(binding) {
        password.setOnEditorActionListener(this@LoginFragment)

        email.afterTextChanged {
            if(isValidateEmail(email.text.toString())) {
                textInputEmail.error = null
            } else {
                textInputEmail.error = getString(R.string.label_insert_email)
            }
        }

        password.afterTextChanged {
            validate(password.text.toString(), binding.textInputPassword, R.string.label_insert_password)
        }

        singIn.setOnClickListener {
            sendLoginData()
        }

        signUp.setOnClickListener {
            findNavController().navigate(
                R.id.action_homeLogin_to_signUp
            )
        }

        recoveryPassword.setOnClickListener {
            findNavController().navigate(
                R.id.action_homeLogin_to_recovery
            )
        }
    }

    private fun validate(s: String, input: TextInputLayout, message: Int) {
        if (s.isNotEmpty()) {
            input.error = null
        } else {
            input.error = resources.getString(message)
        }
    }

    private fun initUser() = with(binding) {
        user = User()
        user!!.email = email.text.toString()
        user!!.password = password.text.toString()
    }

    private fun sendLoginData() = with(binding) {
        when {
            email.text.toString().isEmpty() -> {
                textInputEmail.error = resources.getString(R.string.label_insert_email)
            }
            password.text.toString().isEmpty() -> {
                textInputPassword.error = resources.getString(R.string.label_insert_password)
            }
            else -> {
                openProgressBar()
                initUser()
                verifyLogin()
            }
        }
    }

    private fun verifyLogin() = with(binding) {
        user!!.saveProviderSP(requireContext(), "")
        mAuth!!.signInWithEmailAndPassword(
            user!!.email,
            user!!.password
        )
            .addOnCompleteListener { task: Task<AuthResult?> ->
                if (!task.isSuccessful) {
                    closeProgressBar()
                }
            }.addOnFailureListener { e: Exception ->
                FirebaseCrashlytics.getInstance().recordException(e)
                showSnackBar(e.message, loginProgress)
            }
    }

    override fun onEditorAction(view: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            closeVirtualKeyBoard(requireContext(), requireView())
            sendLoginData()
            return true
        }
        return false
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentLoginBinding.inflate(inflater, container, false)

    private fun openProgressBar() = with(binding) {
        loginProgress.visibility = View.VISIBLE
    }

    private fun closeProgressBar() = with(binding) {
        loginProgress.visibility = View.GONE
    }

    override fun onStart() {
        super.onStart()
        verifyLogged()
    }

    private fun verifyLogged() {
        if (mAuth!!.currentUser != null) {
            callMainActivity()
        } else {
            mAuth!!.addAuthStateListener(mAuthListener!!)
        }
    }

    companion object {
        private const val RC_SIGN_IN_GOOGLE = 7859
        private const val FACEBOOK = "facebook"
        private const val GOOGLE = "google"
        private const val GOOGLE_TOKEN =
            "940299608698-s73ntnnnlbauh7lm0pb8ouis82d279m7.apps.googleusercontent.com"
    }
}