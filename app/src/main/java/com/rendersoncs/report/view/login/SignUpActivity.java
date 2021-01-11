package com.rendersoncs.report.view.login;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rendersoncs.report.R;
import com.rendersoncs.report.infrastructure.constants.ReportConstants;
import com.rendersoncs.report.infrastructure.util.CloseVirtualKeyBoardKt;
import com.rendersoncs.report.view.login.util.LibraryClass;
import com.rendersoncs.report.view.login.util.User;

import java.io.IOException;
import java.util.UUID;

public class SignUpActivity extends CommonActivity implements DatabaseReference.CompletionListener, TextView.OnEditorActionListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private User user;
    private EditText name;
    private Button mBtnSelectedPhoto;
    private Uri mSelectedUri;
    private ImageView mImgPhoto;
    //private TakePicture takePicture = new TakePicture();

    private static final int ALPHA = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        mAuthStateListener = fireBaseAuth -> {
            FirebaseUser firebaseUser = fireBaseAuth.getCurrentUser();

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
        name = findViewById(R.id.sigInName);
        email = findViewById(R.id.sigInEmail);
        password = findViewById(R.id.sigInPassword);
        progressBar = findViewById(R.id.sign_up_progress);

        password.setOnEditorActionListener(this);
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
        /*Toast.makeText(getApplicationContext(), getResources().getString(R.string.version_beta), Toast.LENGTH_SHORT).show();*/
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
                    savePhotoFireBase();
                }
            }).addOnFailureListener(this, e -> {
                closeProgressBar();
                FirebaseCrashlytics.getInstance().recordException(e);
                showSnackBar(e.getMessage());
            });
        }
    }

    private void savePhotoFireBase() {
        String filename = UUID.randomUUID().toString();
        final StorageReference ref = FirebaseStorage.getInstance().getReference("/images/" + filename);
        if (mSelectedUri == null) {
            showSnackBar(getResources().getString(R.string.label_sign_insert_photo));
        } else {
            ref.putFile(mSelectedUri)
                    .addOnSuccessListener(taskSnapshot ->
                            ref.getDownloadUrl()
                                    .addOnSuccessListener(uri -> {
                                        String photoUri = uri.toString();
                                        Log.i("log", "Item: " + photoUri + " profileUrl");

                                        DatabaseReference ref1 = LibraryClass.getFirebase().child(ReportConstants.FIREBASE.FIRE_USERS)
                                                .child(user.getId()).child(ReportConstants.FIREBASE.FIRE_PHOTO);
                                        ref1.setValue(photoUri).addOnSuccessListener(aVoid -> {
                                        });
                                    })).addOnFailureListener(e -> {
                FirebaseCrashlytics.getInstance().recordException(e);
                Toast.makeText(SignUpActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            })
                    //.addOnFailureListener(Crashlytics::logException);
                    .addOnFailureListener(Throwable::printStackTrace);
        }
    }

    private void selectPhoto() {
        Intent it = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(it, ReportConstants.PHOTO.REQUEST_CODE_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ReportConstants.PHOTO.REQUEST_CODE_GALLERY) {
            if (data != null) {
                mSelectedUri = data.getData();
                Bitmap bitmap;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mSelectedUri);
                    mImgPhoto.setImageDrawable(new BitmapDrawable(this.getResources(), bitmap));
                    mBtnSelectedPhoto.setAlpha(ALPHA);

                } catch (IOException e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    showSnackBar(e.getMessage());
                }
            }
        }
    }

    @Override
    public void onComplete(DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
        mAuth.signOut();
        showToast(getResources().getString(R.string.label_account_create));
        closeProgressBar();
        finish();
    }

    public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            CloseVirtualKeyBoardKt.closeVirtualKeyBoard(this, view);
            this.saveUser();
            return true;
        }
        return false;
    }
}