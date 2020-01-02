package com.rendersoncs.reportform.photo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.crashlytics.android.Crashlytics;
import com.rendersoncs.reportform.BuildConfig;
import com.rendersoncs.reportform.constants.ReportConstants;

import java.io.File;

public class TakePicture {

    private CameraUtil path = new CameraUtil();
    private Uri photoUri;

    public void openGallery(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(intent, ReportConstants.PHOTO.REQUEST_CODE_GALLERY);
    }

    public void openCamera(Activity activity) {
        File photoFile;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        ReportConstants.PHOTO.REQUEST_PERMISSIONS_CAMERA);
            } else {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
                    try {
                        photoFile = path.createImageFile();

                        if (photoFile != null) {
                            photoUri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".FileProvider", photoFile);
                            takePictureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);
                            activity.startActivityForResult(takePictureIntent, ReportConstants.PHOTO.REQUEST_CODE_CAMERA);
                            Log.i("LOG", "ImagePathCamera1 " + photoUri + " " + takePictureIntent);
                        }
                    } catch (Exception e) {
                        Toast.makeText(activity.getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Crashlytics.log(e.getMessage());
                    }
                }
            }
        } else {
            try {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                photoFile = path.createImageFile();
                photoUri = Uri.fromFile(photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                activity.startActivityForResult(cameraIntent, ReportConstants.PHOTO.REQUEST_CODE_CAMERA);
            } catch (Exception e) {
                Toast.makeText(activity.getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                Crashlytics.log(e.getMessage());
            }
        }
    }

    public Uri getPathUri() {
        return photoUri;
    }
}
