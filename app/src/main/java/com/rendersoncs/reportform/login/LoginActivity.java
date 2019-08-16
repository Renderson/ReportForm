package com.rendersoncs.reportform.login;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.google.firebase.crash.FirebaseCrash;
import com.rendersoncs.reportform.R;
import com.rendersoncs.reportform.login.util.User;
import com.rendersoncs.reportform.view.MainActivity;

import java.util.Arrays;

public class LoginActivity extends CommonActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN_GOOGLE = 7859;
    public static final String PROFILE_USER_NAME = "NAME";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private User user;
    private CallbackManager callbackManager;
    private GoogleApiClient mGoogleApiClient;
    //private TwitterAuthClient twitterAuthClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // FACEBOOK SIGN IN
        //FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                accessFacebookLoginData(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {}

            @Override
            public void onError(FacebookException error) {
                FirebaseCrash.report(error);
                closeProgressBar();
                showSnackBar("Facebook login falhou, tente novamente!");
            }
        });

        // GOOGLE SIGN IN
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("940299608698-s73ntnnnlbauh7lm0pb8ouis82d279m7.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // TWITTER
        //twitterAuthClient = new TwitterAuthClient();

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
                showSnackBar("Google login falhou, tente novamente!");
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
                "facebook",
                (accessToken != null ? accessToken.getToken() : null)
        );
    }

    // ACCESS GOOGLE
    private void accessGoogleLoginData(String accessToken) {
        accessLoginData(
                "google",
                accessToken
        );
    }

    // ACCESS TWITTER
    private void accessTwitterLoginData(String token, String secret, String id) {
        accessLoginData(
                "twitter",
                token,
                secret
        );
    }

    private void accessLoginData(String provider, String... tokens) {
        if (tokens != null
                && tokens.length > 0
                && tokens[0] != null) {

            AuthCredential credential = FacebookAuthProvider.getCredential(tokens[0]);
            credential = provider.equalsIgnoreCase("google") ? GoogleAuthProvider.getCredential(tokens[0], null) : credential;
            credential = provider.equalsIgnoreCase("twitter") ? TwitterAuthProvider.getCredential(tokens[0], tokens[1]) : credential;
            //credential = provider.equalsIgnoreCase("github") ? GithubAuthProvider.getCredential( tokens[0] ) : credential;

            Log.i("LOG", "Tokens: " + credential);
            user.saveProviderSP(LoginActivity.this, provider);
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                showSnackBar("Login social falhou");
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            FirebaseCrash.report(e);
                        }
                    });
        } else {
            mAuth.signOut();
        }
    }

    private FirebaseAuth.AuthStateListener getFirebaseAuthResultHandler() {
        FirebaseAuth.AuthStateListener callback = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

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
            }
        };
        return (callback);
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
        if (email.getText().toString().isEmpty() || password.getText().toString().isEmpty()){
            Toast.makeText(this, "Por favor! Entre com seus dados.", Toast.LENGTH_SHORT).show();
            return;
        }else {
            openProgressBar();
            initUser();
            verifyLogin();
        }
    }

    // login Facebook
    public void sendLoginFacebookData(View view) {
        FirebaseCrash.log("LoginActivity:clickListener:button:sendLoginFacebookData()");
        LoginManager
                .getInstance()
                .logInWithReadPermissions(
                        this,
                        Arrays.asList("public_profile", "user_friends", "email")
                );
    }

    // login Twitter
//    public void sendLoginTwitterData(View view) {
//        FirebaseCrash.log("LoginActivity:clickListener:button:sendLoginTwitterData()");
//        twitterAuthClient.authorize(
//                this,
//                new Callback<TwitterSession>() {
//                    @Override
//                    public void success(Result<TwitterSession> result) {
//
//                        TwitterSession session = result.data;
//
//                        accessTwitterLoginData(
//                                session.getAuthToken().token,
//                                session.getAuthToken().secret,
//                                String.valueOf( session.getUserId() )
//                        );
//                    }
//                    @Override
//                    public void failure(TwitterException exception) {
//                        FirebaseCrash.report( exception );
//                        showSnackBar( exception.getMessage() );
//                    }
//                }
//        );
//    }

    // Google
    public void sendLoginGoogleData(View view) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
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
        FirebaseCrash.log("LoginActivity:verifyLogin()");
        user.saveProviderSP(LoginActivity.this, "");
        mAuth.signInWithEmailAndPassword(
                user.getEmail(),
                user.getPassword()
        )
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {
                            closeProgressBar();
                            showSnackBar("Login falhou");
                            return;
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                FirebaseCrash.report(e);
            }
        });
    }


    private void callMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    // Google
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        FirebaseCrash.report(new Exception(connectionResult.getErrorCode() + ": " + connectionResult.getErrorMessage()));
        showSnackBar(connectionResult.getErrorMessage());
    }
}
