package com.rendersoncs.reportform.login;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.rendersoncs.reportform.R;
import com.rendersoncs.reportform.login.util.User;

import io.fabric.sdk.android.Fabric;

public class RemoveUserActivity extends AppCompatActivity
        implements ValueEventListener, DatabaseReference.CompletionListener {

    private User user;
    private EditText password;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_user);
        Fabric.with(this, new Crashlytics());

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init(){
        password = findViewById(R.id.password);

        user = new User();
        user.setId( mAuth.getCurrentUser().getUid() );
        user.contextDataDB( this );
    }

    public void removeUser( View view ){
        if (password.getText().toString().isEmpty()) {
            Toast.makeText(this, "Por favor insira a senha.", Toast.LENGTH_SHORT).show();
        } else {
            user.setPassword(password.getText().toString());

            reauthenticate();
        }
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
                .addOnCompleteListener(task -> {

                    if( task.isSuccessful() ){
                        deleteUser();
                    }
                }).addOnFailureListener(e -> {
                    Crashlytics.logException( e );
                    Toast.makeText(
                            RemoveUserActivity.this,
                            e.getMessage(),
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }

    private void deleteUser(){
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if( firebaseUser == null ){
            return;
        }

        firebaseUser.delete().addOnCompleteListener(task -> {

            if( !task.isSuccessful() ){
                return;
            }

            user.removeDB( RemoveUserActivity.this );
        })
                .addOnFailureListener(e -> {
                    Crashlytics.logException( e );
                    Toast.makeText(
                            RemoveUserActivity.this,
                            e.getMessage(),
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        User u = dataSnapshot.getValue( User.class );
        if (u != null) {
            user.setEmail( u.getEmail() );
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError firebaseError) {
        Crashlytics.logException( firebaseError.toException() );
    }

    @Override
    public void onComplete(DatabaseError databaseError,@NonNull DatabaseReference databaseReference) {
        if( databaseError != null ){
            Crashlytics.logException( databaseError.toException() );
        }

        Toast.makeText(
                RemoveUserActivity.this,
                getResources().getString(R.string.label_account_removed),
                Toast.LENGTH_SHORT
        ).show();
        finish();
    }
}