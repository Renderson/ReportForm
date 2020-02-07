package com.rendersoncs.reportform.view.services.photo;

import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CameraUtil {
    private File storageDir;

    public File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HHmmss", new Locale("pt", "br")).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Report-Images");

            if (!storageDir.exists()) {
            storageDir.mkdir();
        }

        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    public File getStorageDir() {
        return storageDir;
    }

}
