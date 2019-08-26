package com.rendersoncs.reportform.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.rendersoncs.reportform.R;
import com.rendersoncs.reportform.login.HomeActivity;
import com.rendersoncs.reportform.login.LoginActivity;
import com.rendersoncs.reportform.login.RemoveUserActivity;
import com.rendersoncs.reportform.login.UpdatePasswordActivity;
import com.rendersoncs.reportform.login.util.LibraryClass;
import com.rendersoncs.reportform.util.GetInfoUserFirebase;

public class ConfigAccountActivity extends AppCompatActivity {

    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference databaseReference;
    private GetInfoUserFirebase info = new GetInfoUserFirebase();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity_config);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Configuração de conta");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        authStateListener = firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() == null) {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        };
    }

    private void init() {
        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener( authStateListener );
        user = mAuth.getCurrentUser();
        databaseReference = LibraryClass.getFirebase();

        ImageView profileImage = findViewById(R.id.img_profile);
        TextView profileName = findViewById(R.id.txt_config_name);
        TextView profileEmail = findViewById(R.id.txt_config_mail);

        info.getInfoUserFirebase(this, user, databaseReference, profileName, profileEmail, profileImage);
    }

    public void updatePassword(View view){
        Intent i = (new Intent(this, UpdatePasswordActivity.class));
        startActivity(i);
    }

    public void remove(View view){
        Intent i = (new Intent(this, RemoveUserActivity.class));
        startActivity(i);
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (authStateListener != null) {
            mAuth.removeAuthStateListener(authStateListener);
        }
    }
}
