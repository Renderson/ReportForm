/*package com.rendersoncs.reportform.view.activitys.login;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.rendersoncs.reportform.R;
import com.rendersoncs.reportform.view.activitys.login.util.LibraryClass;
import com.rendersoncs.reportform.view.activitys.login.util.User;

public class UpdateLoginActivity extends AppCompatActivity implements ValueEventListener, Firebase.CompletionListener {

    private Toolbar toolbar;
    private User user;
    private EditText newEmail;
    private EditText password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_login);

        //toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
    }

    protected void onResume(){
        super.onResume();
        init();
    }

    private void init(){
        toolbar.setTitle("atualiza login");
        newEmail = findViewById(R.id.email);
        password = findViewById(R.id.password);

        user = new User();
        user.contextDataDB(this);
    }

    public void update(View view){

        user.setPassword(password.getText().toString());
        //user.generateCrytPassword();

        Firebase firebase = LibraryClass.getFirebase();
        firebase.changeEmail(user.getEmail(),
                user.getPassword(),
                newEmail.getText().toString(),
                new Firebase.ResultHandler() {
            @Override
            public void onSuccess() {
                user.setEmail(newEmail.getText().toString());
                user.updateDB();
                Toast.makeText(UpdateLoginActivity.this, "Email de login atualizado com sucesso", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                Toast.makeText(UpdateLoginActivity.this, firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        User u = dataSnapshot.getValue(User.class);
        newEmail.setText(u.getEmail());
        user.setEmail(u.getEmail());
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) { }

    @Override
    public void onComplete(FirebaseError firebaseError, Firebase firebase) {}
}


*/