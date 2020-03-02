package com.rendersoncs.reportform.view.activitys;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.rendersoncs.reportform.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class SignatureActivityMain extends AppCompatActivity {

    private SignaturePad mSignaturePad;
    private Button mClearButton;
    private Button mSaveButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);

        mSignaturePad = findViewById(R.id.signature_pad);
        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
                //Toast.makeText(SignatureActivityMain.this, "Signature Start", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSigned() {
                mSaveButton.setEnabled(true);
                mClearButton.setEnabled(true);
            }

            @Override
            public void onClear() {
                mSaveButton.setEnabled(false);
                mClearButton.setEnabled(false);
            }
        });

        mSaveButton = findViewById(R.id.save_button);
        mClearButton = findViewById(R.id.clear_button);

        mClearButton.setOnClickListener(v -> mSignaturePad.clear());
        mSaveButton.setOnClickListener(v -> {
            Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();
            if (addSignatureToGallery(signatureBitmap)){
                Toast.makeText(this, "Signature saved into the gallery", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Unable to store the signature", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean addSignatureToGallery(Bitmap signature) {
        boolean result = false;
        try {
            File photo = new File(getAlbumStorageDir("Report-Images"), String.format("Signature.jpg"));
            saveBitmapToJPG(signature, photo);
            //scanMediaFile(photo);
            result = true;
        } catch (IOException e){
            e.printStackTrace();
        }
        return result;
    }

    private File getAlbumStorageDir(String albumName) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdir()){
            Log.e("SignaturePad", "Directory not created");
        }
        return file;
    }

    private void saveBitmapToJPG(Bitmap signature, File photo) throws IOException {
        Bitmap newBitmap = Bitmap.createBitmap(signature.getWidth(), signature.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(signature, 0, 0, null);
        OutputStream stream = new FileOutputStream(photo);
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        stream.close();
    }
}
