package com.rendersoncs.reportform.repository.net;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;
import com.rendersoncs.reportform.view.services.constants.ReportConstants;
import com.rendersoncs.reportform.view.activitys.login.util.User;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.content.ContentValues.TAG;

public class DownloadJsonFireBaseAsyncTask extends AsyncTask<Void, Void, String> {
    private User user;

    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG, "onPreExecute call");
    }

    @Override
    protected String doInBackground(Void... voids) {
        Log.d(TAG, "doInBackground AWS call ");
        try {

            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            user = new User();
            user.setId( mAuth.getCurrentUser().getUid() );

            URL url = new URL(ReportConstants.FIRE_BASE.URL + "/" +user.getId() +".json");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder stringBuilder = new StringBuilder();

            String json_GetString;
            while ((json_GetString = bufferedReader.readLine()) != null){
                stringBuilder.append(json_GetString).append("\n");
            }

            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();

            return stringBuilder.toString().trim();

        } catch (IOException e) {
            Crashlytics.logException(e);
        }
        return null;
    }

    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.d(TAG, "onPostExecute call");

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Report" + "/" + user.getId() + ".json");

        FileWriter fos = null;
        try {
            fos = new FileWriter(file);
            fos.write(result);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
