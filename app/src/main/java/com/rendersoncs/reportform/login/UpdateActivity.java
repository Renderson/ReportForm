/*package com.rendersoncs.reportform.login;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
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
import com.rendersoncs.reportform.login.util.User;

public class UpdateActivity extends AppCompatActivity implements ValueEventListener, Firebase.CompletionListener {

    private Toolbar toolbar;
    private User user;
    private TextView name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        //toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
    }

    protected void onResume(){
        super.onResume();
        init();
    }

    private void init(){
        toolbar.setTitle("atualiza perfil");
        name = findViewById(R.id.name);
        user = new User();
        user.contextDataDB(this);
    }

    public void update(View view){
        user.retrieveIdSP(this);
        user.setName(name.getText().toString());
        user.updateDB(this);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        User u = dataSnapshot.getValue(User.class);
        name.setText(u.getName());
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) { }

    @Override
    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
        if (firebaseError != null){
            Toast.makeText(this, "Falhou"+firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Alteração com sucesso", Toast.LENGTH_SHORT).show();
        }
    }
}

*/