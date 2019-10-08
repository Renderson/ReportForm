//package com.rendersoncs.reportform.photo;
//
//import android.Manifest;
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.graphics.Bitmap;
//import android.net.Uri;
//import android.os.Build;
//import android.provider.MediaStore;
//import android.util.Log;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//import androidx.core.content.FileProvider;
//
//import com.crashlytics.android.Crashlytics;
//import com.rendersoncs.reportform.BuildConfig;
//import com.rendersoncs.reportform.adapter.ReportCheckListAdapter;
//import com.rendersoncs.reportform.view.ReportActivity;
//
//import java.io.File;
//import java.io.IOException;
//
//abstract public class TakePicture extends AppCompatActivity {
//
//
//    private static final int REQUEST_CODE_CAMERA = 2000;
//    private static final int REQUEST_CODE_GALLERY = 2006;
//    private static final int REQUEST_PERMISSIONS_CAMERA = 0;
//    private static final int REQUEST_PERMISSIONS_READ_WHITE = 128;
//
//    public ReportCheckListAdapter mAdapter;
//    private CameraUtil path = new CameraUtil();
//    Uri photoUri;
//    private int position;
//    private Context context;
//
//    protected void openGallery1() {
//        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(intent, REQUEST_CODE_GALLERY);
//    }
//
//    public void openCamera1(Activity activity) {
//        File photoFile;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_CAMERA);
//            } else {
//                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//                    try {
//                        //photoFile = createImageFile();
//                        photoFile = path.createImageFile();
//
//                        if (photoFile != null) {
//                            Uri photoUri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".FileProvider", photoFile);
//                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
//                            startActivityForResult(takePictureIntent, REQUEST_CODE_CAMERA);
//                        }
//                    } catch (Exception e) {
//                        Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                        Crashlytics.log(e.getMessage());
//                    }
//                }
//            }
//        } else {
//            try {
//                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                photoFile = path.createImageFile(); // verificar versão
//                photoUri = Uri.fromFile(photoFile);
//                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
//                startActivityForResult(cameraIntent, REQUEST_CODE_CAMERA);
//            } catch (Exception e) {
//                Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                Crashlytics.log(e.getMessage());
//            }
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode != Activity.RESULT_CANCELED) {
//            if (requestCode == REQUEST_CODE_CAMERA && resultCode == RESULT_OK) {
//
//                ResizeImage.decodeBitmap(photoUri, mAdapter, position);
//                //radioItemChecked(position, 1);
//            }
//        }
//        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null) {
//            Uri mSelectedUri = data.getData();
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mSelectedUri);
//                mAdapter.setImageInItem(position, bitmap);
//                //radioItemChecked(position, 1);
//                Log.i("LOG", "ImagePath " + bitmap);
//
//            } catch (IOException e) {
//                Crashlytics.logException(e);
//            }
//        } else {
//            super.onActivityResult(requestCode, resultCode, data);
//        }
//    }
//
//}
