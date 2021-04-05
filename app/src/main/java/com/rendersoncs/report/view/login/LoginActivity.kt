package com.rendersoncs.report.view.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.rendersoncs.report.R
import com.rendersoncs.report.databinding.ActivityLoginBinding
import com.rendersoncs.report.infrastructure.util.closeVirtualKeyBoard
import com.rendersoncs.report.view.login.util.User
import com.rendersoncs.report.view.main.MainActivity
import java.util.*

class LoginActivity : CommonActivity(), OnEditorActionListener {
    private var mAuth: FirebaseAuth? = null
    private var mAuthListener: AuthStateListener? = null
    private var user: User? = null
    private var callbackManager: CallbackManager? = null
    private var mGoogleApiClient: GoogleSignInClient? = null
    private lateinit var binding: ActivityLoginBinding

    /*private GoogleApiClient mGoogleApiClient;
    private TwitterAuthClient twitterAuthClient;*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)

        // FACEBOOK SIGN IN
        //FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                accessFacebookLoginData(loginResult.accessToken)
            }

            override fun onCancel() {}
            override fun onError(error: FacebookException) {
                FirebaseCrashlytics.getInstance().recordException(error)
                closeProgressBar()
                showSnackBar(resources.getString(R.string.label_login_facebook_failed))
            }
        })

        // GOOGLE SIGN IN
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(GOOGLE_TOKEN)
                .requestEmail()
                .build()
        mGoogleApiClient = GoogleSignIn.getClient(this, gso)
        /*mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();*/

        // TWITTER
        /*twitterAuthClient = new TwitterAuthClient();*/mAuth = FirebaseAuth.getInstance()
        mAuthListener = firebaseAuthResultHandler
        initViews()
        initUser()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN_GOOGLE) {
            val googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            val account = googleSignInResult!!.signInAccount
            if (account == null) {
                showSnackBar(resources.getString(R.string.label_google_failed))
                return
            }
            accessGoogleLoginData(account.idToken)
        } else {
            //twitterAuthClient.onActivityResult(requestCode, resultCode, data);
            callbackManager!!.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onStart() {
        super.onStart()
        verifyLogged()
    }

    override fun onStop() {
        super.onStop()
        if (mAuthListener != null) {
            mAuth!!.removeAuthStateListener(mAuthListener!!)
        }
    }

    // ACCESS FACEBOOK
    private fun accessFacebookLoginData(accessToken: AccessToken?) {
        accessLoginData(
                FACEBOOK,
                accessToken?.token
        )
    }

    // ACCESS GOOGLE
    private fun accessGoogleLoginData(accessToken: String?) {
        accessLoginData(
                GOOGLE,
                accessToken
        )
    }

    // ACCESS TWITTER
    /*private void accessTwitterLoginData(String token, String secret, String id) {
        accessLoginData(
                "twitter",
                token,
                secret
        );
    }*/
    private fun accessLoginData(provider: String, vararg tokens: String?) {
        if (tokens != null && tokens.size > 0 && tokens[0] != null) {
            var credential: AuthCredential? = FacebookAuthProvider.getCredential(tokens[0]!!)
            credential = if (provider.equals(GOOGLE, ignoreCase = true)) GoogleAuthProvider.getCredential(tokens[0], null) else credential
            credential = if (provider.equals("twitter", ignoreCase = true)) TwitterAuthProvider.getCredential(tokens[0]!!, tokens[1]!!) else credential
            //credential = provider.equalsIgnoreCase("github") ? GithubAuthProvider.getCredential( tokens[0] ) : credential;
            user!!.saveProviderSP(this@LoginActivity, provider)
            mAuth!!.signInWithCredential(credential!!)
                    .addOnCompleteListener { task: Task<AuthResult?> ->
                        if (!task.isSuccessful) {
                            showSnackBar(resources.getString(R.string.label_login_social_failed))
                        }
                    }
                    .addOnFailureListener { obj: Exception -> obj.printStackTrace() }
        } else {
            mAuth!!.signOut()
        }
    }

    private val firebaseAuthResultHandler: AuthStateListener
        get() = AuthStateListener { firebaseAuth: FirebaseAuth ->
            val userFirebase = firebaseAuth.currentUser ?: return@AuthStateListener
            if (user!!.id == null
                    && isNameOk(user, userFirebase)) {
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

    override fun initViews() {
        progressBar = findViewById(R.id.login_progress)

        binding.email.addTextChangedListener(emailWatcher)
        binding.password.addTextChangedListener(passwordWatcher)
        binding.password.setOnEditorActionListener(this)
    }

    private val emailWatcher = object: TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(s: Editable?) {
            validate(s, binding.textInputEmail, R.string.label_insert_email)
        }
    }

    private val passwordWatcher = object: TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(s: Editable?) {
            validate(s, binding.textInputPassword, R.string.label_insert_password)
        }
    }

    override fun initUser() {
        user = User()
        user!!.email = binding.email.text.toString()
        user!!.password = binding.password.text.toString()
    }

    fun callSignUp(view: View?) {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }

    fun callReset(view: View?) {
        val intent = Intent(this, RecoveryLoginActivity::class.java)
        startActivity(intent)
    }

    fun sendLoginData(view: View?) {
        when {
            binding.email.text.toString().isEmpty() -> {
                binding.textInputEmail.error = resources.getString(R.string.label_insert_email)
            }
            binding.password.text.toString().isEmpty() -> {
                binding.textInputPassword.error = resources.getString(R.string.label_insert_password)
            }
            else -> {
                openProgressBar()
                initUser()
                verifyLogin()
            }
        }
    }

    override fun onEditorAction(view: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            closeVirtualKeyBoard(this, view!!)
            sendLoginData(view)
            return true
        }
        return false
    }

    // login Facebook
    fun sendLoginFacebookData(view: View?) {
        /*Toast.makeText(getApplicationContext(), getResources().getString(R.string.version_beta), Toast.LENGTH_SHORT).show();*/
        LoginManager
                .getInstance()
                .logInWithReadPermissions(
                        this,
                        Arrays.asList("public_profile", "user_friends", "email")
                )
    }

    // login Twitter
    /*public void sendLoginTwitterData(View view) {
        FirebaseCrash.log("LoginActivity:clickListener:button:sendLoginTwitterData()");
        twitterAuthClient.authorize(
                this,
                new Callback<TwitterSession>() {
                    @Override
                    public void success(Result<TwitterSession> result) {

                        TwitterSession session = result.data;

                        accessTwitterLoginData(
                                session.getAuthToken().token,
                                session.getAuthToken().secret,
                                String.valueOf( session.getUserId() )
                        );
                    }
                    @Override
                    public void failure(TwitterException exception) {
                        FirebaseCrash.report( exception );
                        showSnackBar( exception.getMessage() );
                    }
                }
        );
    }*/
    // Google
    fun sendLoginGoogleData(view: View?) {
        /*Toast.makeText(getApplicationContext(), getResources().getString(R.string.version_beta), Toast.LENGTH_SHORT).show();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);*/
        val signInIntent = mGoogleApiClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN_GOOGLE)
    }

    private fun verifyLogged() {
        if (mAuth!!.currentUser != null) {
            callMainActivity()
        } else {
            mAuth!!.addAuthStateListener(mAuthListener!!)
        }
    }

    private fun verifyLogin() {
        user!!.saveProviderSP(this@LoginActivity, "")
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
                    showSnackBar(e.message)
                }
    }

    private fun callMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    } // Google

    /*@Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Crashlytics.logException(new Exception(connectionResult.getErrorCode() + ": " + connectionResult.getErrorMessage()));
        showSnackBar(connectionResult.getErrorMessage());
    }*/
    companion object {
        private const val RC_SIGN_IN_GOOGLE = 7859
        private const val FACEBOOK = "facebook"
        private const val GOOGLE = "google"
        private const val GOOGLE_TOKEN = "940299608698-s73ntnnnlbauh7lm0pb8ouis82d279m7.apps.googleusercontent.com"
    }
}