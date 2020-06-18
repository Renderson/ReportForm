package com.rendersoncs.reportform.view.services.photo;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.rendersoncs.reportform.itens.ReportItems;
import com.rendersoncs.reportform.view.adapter.checkListAdapter.ReportAdapter;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ResizeImage {

    private static final int WANTED_WIDTH = 640;
    private static final int WANTED_HEIGHT = 480;

    // Convert image Base64 String
    public String getEncoded64Image(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] byteFormat = stream.toByteArray();
        String imgString = Base64.encodeToString(byteFormat, Base64.DEFAULT);
        Log.d("Base64 ", imgString);

        return imgString;
    }

    private static Bitmap scaleBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(ResizeImage.WANTED_WIDTH, ResizeImage.WANTED_HEIGHT, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Matrix m = new Matrix();
        m.setScale((float) ResizeImage.WANTED_WIDTH / bitmap.getWidth(), (float) ResizeImage.WANTED_HEIGHT / bitmap.getHeight());
        canvas.drawBitmap(bitmap, m, new Paint());

        return output;
    }

    public static void decodeBitmap(Uri photoUri, ReportAdapter mAdapter, ReportItems reportItems) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        ContentResolver cr = getApplicationContext().getContentResolver();
        InputStream input = null;
        InputStream input1 = null;
        try {
            input = cr.openInputStream(photoUri);
            BitmapFactory.decodeStream(input, null, bmOptions);
            if (input != null) {
                input.close();
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
        }

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        try {
            input1 = cr.openInputStream(photoUri);
            Bitmap takenImage = BitmapFactory.decodeStream(input1);
            Bitmap photo = scaleBitmap(takenImage);
            mAdapter.setImageInItem(reportItems, photo);
            if (input1 != null) {
                input1.close();
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }
}
