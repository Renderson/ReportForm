package com.rendersoncs.reportform.login;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rendersoncs.reportform.R;
import com.rendersoncs.reportform.login.util.LibraryClass;
import com.rendersoncs.reportform.login.util.User;

import java.io.IOException;
import java.util.UUID;

import io.fabric.sdk.android.Fabric;

public class SignUpActivity extends CommonActivity implements DatabaseReference.CompletionListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private User user;
    private EditText name;
    private Button mBtnSelectedPhoto;
    private Uri mSelectedUri;
    private ImageView mImgPhoto;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Fabric.with(this, new Crashlytics());

        mAuth = FirebaseAuth.getInstance();

        mAuthStateListener = firebaseAuth -> {
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

            if (firebaseUser == null || user.getId() != null) {
                return;
            }

            user.setId(firebaseUser.getUid());
            user.saveDB(SignUpActivity.this);
        };

        mImgPhoto = findViewById(R.id.img_photo);
        mBtnSelectedPhoto = findViewById(R.id.btn_select_photo);
        mBtnSelectedPhoto.setOnClickListener(view -> selectPhoto());

        initViews();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    protected void initViews() {
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        progressBar = findViewById(R.id.sign_up_progress);
    }

    protected void initUser() {
        user = new User();
        user.setName(name.getText().toString());
        user.setEmail(email.getText().toString());
        user.setPassword(password.getText().toString());
    }

    public void sendSignUpData(View view) {
        openProgressBar();
        initUser();
        saveUser();
        // For Version Test
//        Toast.makeText(getApplicationContext(), getResources().getString(R.string.version_beta), Toast.LENGTH_SHORT).show();
    }

    public void callLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void saveUser() {
        if (name.getText().toString().isEmpty()
                || email.getText().toString().isEmpty()
                || password.getText().toString().isEmpty() || mSelectedUri == null) {

            showSnackBar(getResources().getString(R.string.label_insert_data_create_account));
            closeProgressBar();

        } else {

            mAuth.createUserWithEmailAndPassword(
                    user.getEmail(),
                    user.getPassword()
            ).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    closeProgressBar();
                    savePhotoFirebase();
                }
            }).addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    closeProgressBar();
                    Crashlytics.logException(e);
                    showSnackBar(e.getMessage());
                }
            });
        }
    }

    private void savePhotoFirebase() {
        String filename = UUID.randomUUID().toString();
        final StorageReference ref = FirebaseStorage.getInstance().getReference("/images/" + filename);
        if (mSelectedUri == null) {
            showSnackBar("Insira foto");
            return;
        } else {
            ref.putFile(mSelectedUri)
                    .addOnSuccessListener(taskSnapshot ->
                            ref.getDownloadUrl()
                                    .addOnSuccessListener(uri -> {
                                        String photoUri = uri.toString();
                                        Log.i("log", "Item: " + photoUri + " profileUrl");

                                        DatabaseReference ref1 = LibraryClass.getFirebase().child("users").child(user.getId()).child("photoUrl");
                                        ref1.setValue(photoUri).addOnSuccessListener(aVoid -> {
                                        });
                                    })).addOnFailureListener(e -> {
                Crashlytics.logException(e);
                Toast.makeText(SignUpActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            })
                    .addOnFailureListener(Crashlytics::logException);
        }
    }

    private void selectPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            if (data != null) {
                mSelectedUri = data.getData();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mSelectedUri);
                    mImgPhoto.setImageDrawable(new BitmapDrawable(this.getResources(), bitmap));
                    mBtnSelectedPhoto.setAlpha(0);

                } catch (IOException e) {
                    Crashlytics.logException(e);
                    showSnackBar(e.getMessage());
                }
            }
        } else {
            return;
        }
    }

    @Override
    public void onComplete(DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
        mAuth.signOut();
        showToast(getResources().getString(R.string.label_account_create));
        closeProgressBar();
        finish();
    }
}
