package com.rendersoncs.reportform.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.rendersoncs.reportform.R;

public class RecoveryLoginActivity extends AppCompatActivity {

    private EditText email;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery_login);

        TextView returnLoginActivity = findViewById(R.id.sendHome);
        returnLoginActivity.setOnClickListener(view -> callLoginActivity());

        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void callLoginActivity() {
        Intent i = (new Intent(this, LoginActivity.class));
        startActivity(i);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init(){
        email = findViewById(R.id.email);
    }

    public void reset( View view ){
        if (email.getText().toString().isEmpty()){
            Toast.makeText(this, getResources().getString(R.string.label_insert_email), Toast.LENGTH_SHORT).show();
            return;
        }else {
            firebaseAuth
                    .sendPasswordResetEmail(email.getText().toString())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            email.setText("");
                            Toast.makeText(
                                    RecoveryLoginActivity.this,
                                    getResources().getString(R.string.label_recover_access_send),
                                    Toast.LENGTH_SHORT
                            ).show();
                        } else {
                            Toast.makeText(
                                    RecoveryLoginActivity.this,
                                    getResources().getString(R.string.label_falied),
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Crashlytics.logException(e);
                        }
                    });
        }
    }
}