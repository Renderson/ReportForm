/*package com.rendersoncs.reportform.login;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.rendersoncs.reportform.R;
import com.rendersoncs.reportform.login.util.LibraryClass;
import com.rendersoncs.reportform.login.util.User;

public class UpdatePasswordActivity extends AppCompatActivity implements ValueEventListener, Firebase.CompletionListener {

    private Toolbar toolbar;
    private User user;
    private EditText newPassword;
    private EditText password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);

        //toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
    }

    protected void onResume(){
        super.onResume();
        init();
    }

    private void init(){
        toolbar.setTitle("atualiza Senha");
        newPassword = findViewById(R.id.password);
        password = findViewById(R.id.password);

        user = new User();
        user.contextDataDB(this);
    }

    public void update(View view){

        user.setPassword(password.getText().toString());
        //user.generateCrytNewPassword();
        user.setPassword(password.getText().toString());
        //user.generateCrytPassword();

        Firebase firebase = LibraryClass.getFirebase();
        firebase.changePassword(user.getEmail(),
                user.getPassword(),
                user.getNewPassword(),
                new Firebase.ResultHandler() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(UpdatePasswordActivity.this, "Senha atualizado com sucesso", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FirebaseError firebaseError) {
                        Toast.makeText(UpdatePasswordActivity.this, firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        User u = dataSnapshot.getValue(User.class);
        user.setEmail(u.getEmail());
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) { }

    @Override
    public void onComplete(FirebaseError firebaseError, Firebase firebase) {}
}



*/