package com.rendersoncs.reportform.login;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
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
    private Toolbar toolbar;
    private User user;
    private EditText newPassword;
    private EditText password;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
        Fabric.with(this, new Crashlytics());

//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init(){
        //toolbar.setTitle( getResources().getString(R.string.update_password) );
        newPassword = findViewById(R.id.new_password);
        password = findViewById(R.id.password);

        user = new User();
        user.setId( mAuth.getCurrentUser().getUid() );
        user.contextDataDB( this );
    }

    public void update( View view ){
        user.setNewPassword( newPassword.getText().toString() );
        user.setPassword( password.getText().toString() );

        reauthenticate();
    }

    private void reauthenticate(){
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if( firebaseUser == null ){
            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(
                user.getEmail(),
                user.getPassword()
        );

        firebaseUser.reauthenticate( credential )
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if( task.isSuccessful() ){
                            updateData();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Crashlytics.logException( e );
                        Toast.makeText(
                                UpdatePasswordActivity.this,
                                e.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
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
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if( task.isSuccessful() ){
                            newPassword.setText("");
                            password.setText("");

                            Toast.makeText(
                                    UpdatePasswordActivity.this,
                                    getResources().getString(R.string.label_password_update),
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Crashlytics.logException( e );
                        Toast.makeText(
                                UpdatePasswordActivity.this,
                                e.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        User u = dataSnapshot.getValue( User.class );
        user.setEmail( u.getEmail() );
    }

    @Override
    public void onCancelled(DatabaseError firebaseError) {
        Crashlytics.logException( firebaseError.toException() );
    }
}