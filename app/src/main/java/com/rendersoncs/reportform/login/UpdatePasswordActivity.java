package com.rendersoncs.reportform.login;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.rendersoncs.reportform.R;
import com.rendersoncs.reportform.login.util.User;

import io.fabric.sdk.android.Fabric;

public class UpdatePasswordActivity extends AppCompatActivity implements ValueEventListener {
    private User user;
    private EditText newPassword;
    private EditText password;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
        Fabric.with(this, new Crashlytics());

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init(){
        newPassword = findViewById(R.id.new_password);
        password = findViewById(R.id.password);

        user = new User();
        user.setId( mAuth.getCurrentUser().getUid() );
        user.contextDataDB( this );
    }

    public void update( View view ){
        if (newPassword.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.label_insert_password), Toast.LENGTH_SHORT).show();
        } else {
            user.setNewPassword(newPassword.getText().toString());
            user.setPassword(password.getText().toString());

            reAuthenticate();
        }
    }

    private void reAuthenticate(){
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if( firebaseUser == null ){
            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(
                user.getEmail(),
                user.getPassword()
        );

        firebaseUser.reauthenticate( credential )
                .addOnCompleteListener(task -> {

                    if( task.isSuccessful() ){
                        updateData();
                    }
                })
                .addOnFailureListener(e -> {
                    Crashlytics.logException( e );
                    Toast.makeText(
                            UpdatePasswordActivity.this,
                            e.getMessage(),
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }

    private void updateData(){
        user.setNewPassword( newPassword.getText().toString() );
        user.setPassword( password.getText().toString() );

        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if( firebaseUser == null ){
            return;
        }

        firebaseUser
                .updatePassword( user.getNewPassword() )
                .addOnCompleteListener(task -> {

                    if( task.isSuccessful() ){
                        newPassword.setText("");
                        password.setText("");

                        Toast.makeText(
                                UpdatePasswordActivity.this,
                                getResources().getString(R.string.label_password_update),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                })
                .addOnFailureListener(this, e -> {
                    Crashlytics.logException( e );
                    Toast.makeText(
                            UpdatePasswordActivity.this,
                            e.getMessage(),
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        User u = dataSnapshot.getValue( User.class );
        if (u != null) {
            user.setEmail( u.getEmail() );
        }
    }

    @Override
    public void onCancelled(DatabaseError fireBaseError) {
        Crashlytics.logException( fireBaseError.toException() );
    }
}