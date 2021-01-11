package com.rendersoncs.report.view.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.rendersoncs.report.R;
import com.rendersoncs.report.infrastructure.util.CloseVirtualKeyBoardKt;

public class RecoveryLoginActivity extends AppCompatActivity implements TextView.OnEditorActionListener {

    private EditText email;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery_login);

        TextView returnLoginActivity = findViewById(R.id.sendHome);
        returnLoginActivity.setOnClickListener(view -> callLoginActivity());

        firebaseAuth = FirebaseAuth.getInstance();
        init();
    }

    private void callLoginActivity() {
        Intent i = (new Intent(this, LoginActivity.class));
        startActivity(i);
        finish();
    }

    private void init(){
        email = findViewById(R.id.emailRecover);
        email.setOnEditorActionListener(this);
    }

    public void reset( View view ){
        if (email.getText().toString().isEmpty()){
            Toast.makeText(this, getResources().getString(R.string.label_insert_email), Toast.LENGTH_SHORT).show();
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
                                    getResources().getString(R.string.label_failed),
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    })
                    .addOnFailureListener(Throwable::printStackTrace);
        }
    }

    public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            CloseVirtualKeyBoardKt.closeVirtualKeyBoard(this, view);
            this.reset(view);
            return true;
        }
        return false;
    }
}