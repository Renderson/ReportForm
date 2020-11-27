package com.rendersoncs.report.view.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.rendersoncs.report.R;
import com.rendersoncs.report.view.main.MainActivity;
import com.rendersoncs.report.view.login.util.User;

import java.util.Arrays;

public class LoginActivity extends CommonActivity {

    private static final int RC_SIGN_IN_GOOGLE = 7859;
    private static final String FACEBOOK = "facebook";
    private static final String GOOGLE = "google";
    private static final String GOOGLE_TOKEN = "940299608698-s73ntnnnlbauh7lm0pb8ouis82d279m7.apps.googleusercontent.com";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private User user;
    private CallbackManager callbackManager;
    private GoogleSignInClient mGoogleApiClient;
    /*private GoogleApiClient mGoogleApiClient;
    private TwitterAuthClient twitterAuthClient;*/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);

        // FACEBOOK SIGN IN
        //FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                accessFacebookLoginData(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() { }

            @Override
            public void onError(FacebookException error) {
                FirebaseCrashlytics.getInstance().recordException(error);
                closeProgressBar();
                showSnackBar(getResources().getString(R.string.label_login_facebook_failed));
            }
        });

        // GOOGLE SIGN IN
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(GOOGLE_TOKEN)
                .requestEmail()
                .build();

        mGoogleApiClient = GoogleSignIn.getClient(this, gso);
        /*mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();*/

        // TWITTER
        /*twitterAuthClient = new TwitterAuthClient();*/

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = getFirebaseAuthResultHandler();
        initViews();
        initUser();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN_GOOGLE) {

            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            GoogleSignInAccount account = googleSignInResult.getSignInAccount();

            if (account == null) {
                showSnackBar(getResources().getString(R.string.label_google_failed));
                return;
            }

            accessGoogleLoginData(account.getIdToken());

        } else {
            //twitterAuthClient.onActivityResult(requestCode, resultCode, data);
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        verifyLogged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    // ACCESS FACEBOOK
    private void accessFacebookLoginData(AccessToken accessToken) {
        accessLoginData(
                FACEBOOK,
                (accessToken != null ? accessToken.getToken() : null)
        );
    }

    // ACCESS GOOGLE
    private void accessGoogleLoginData(String accessToken) {
        accessLoginData(
                GOOGLE,
                accessToken
        );
    }

    // ACCESS TWITTER
    /*private void accessTwitterLoginData(String token, String secret, String id) {
        accessLoginData(
                "twitter",
                token,
                secret
        );
    }*/

    private void accessLoginData(String provider, String... tokens) {
        if (tokens != null
                && tokens.length > 0
                && tokens[0] != null) {

            AuthCredential credential = FacebookAuthProvider.getCredential(tokens[0]);
            credential = provider.equalsIgnoreCase(GOOGLE) ? GoogleAuthProvider.getCredential(tokens[0], null) : credential;
            credential = provider.equalsIgnoreCase("twitter") ? TwitterAuthProvider.getCredential(tokens[0], tokens[1]) : credential;
            //credential = provider.equalsIgnoreCase("github") ? GithubAuthProvider.getCredential( tokens[0] ) : credential;

            user.saveProviderSP(LoginActivity.this, provider);
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            showSnackBar(getResources().getString(R.string.label_login_social_failed));
                        }
                    })
                    .addOnFailureListener(Throwable::printStackTrace);
        } else {
            mAuth.signOut();
        }
    }

    private FirebaseAuth.AuthStateListener getFirebaseAuthResultHandler() {
        return (firebaseAuth -> {

            FirebaseUser userFirebase = firebaseAuth.getCurrentUser();

            if (userFirebase == null) {
                return;
            }

            if (user.getId() == null
                    && isNameOk(user, userFirebase)) {

                user.setId(userFirebase.getUid());
                user.setNameIfNull(userFirebase.getDisplayName());
                user.setEmailIfNull(userFirebase.getEmail());
                user.saveDB();
            }

            callMainActivity();
        });
    }

    private boolean isNameOk(User user, FirebaseUser firebaseUser) {
        return (
                user.getName() != null
                        || firebaseUser.getDisplayName() != null
        );
    }


    @Override
    protected void initViews() {
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        progressBar = findViewById(R.id.login_progress);
    }

    @Override
    protected void initUser() {
        user = new User();
        user.setEmail(email.getText().toString());
        user.setPassword(password.getText().toString());
    }

    public void callSignUp(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    public void callReset(View view) {
        Intent intent = new Intent(this, RecoveryLoginActivity.class);
        startActivity(intent);
    }

    public void sendLoginData(View view) {
        if (email.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
            showSnackBar(getResources().getString(R.string.label_please_entry_data));
        } else {
            openProgressBar();
            initUser();
            verifyLogin();
        }
    }

    // login Facebook
    public void sendLoginFacebookData(View view) {
        /*Toast.makeText(getApplicationContext(), getResources().getString(R.string.version_beta), Toast.LENGTH_SHORT).show();*/
        LoginManager
                .getInstance()
                .logInWithReadPermissions(
                        this,
                        Arrays.asList("public_profile", "user_friends", "email")
                );
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
    public void sendLoginGoogleData(View view) {
        /*Toast.makeText(getApplicationContext(), getResources().getString(R.string.version_beta), Toast.LENGTH_SHORT).show();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);*/
        Intent signInIntent = mGoogleApiClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN_GOOGLE);
    }


    private void verifyLogged() {
        if (mAuth.getCurrentUser() != null) {
            callMainActivity();
        } else {
            mAuth.addAuthStateListener(mAuthListener);
        }
    }

    private void verifyLogin() {
        user.saveProviderSP(LoginActivity.this, "");
        mAuth.signInWithEmailAndPassword(
                user.getEmail(),
                user.getPassword()
        )
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {
                        closeProgressBar();
                    }
                }).addOnFailureListener(e -> {
            FirebaseCrashlytics.getInstance().recordException(e);
            showSnackBar(e.getMessage());
        });
    }

    private void callMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    // Google
    /*@Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Crashlytics.logException(new Exception(connectionResult.getErrorCode() + ": " + connectionResult.getErrorMessage()));
        showSnackBar(connectionResult.getErrorMessage());
    }*/
}
