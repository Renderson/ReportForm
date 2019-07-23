package com.rendersoncs.reportform.async;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.rendersoncs.reportform.constants.ReportConstants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.content.ContentValues.TAG;

public class DownloadJsonFireBaseAsyncTask extends AsyncTask<Void, Void, String> {
    private Context context;

    public DownloadJsonFireBaseAsyncTask(Context context){
        this.context = context;
    }

    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG, "onPreExecute call");
    }

    @Override
    protected String doInBackground(Void... voids) {
        Log.d(TAG, "doInBackground AWS call ");
        try {
            String json_url = ReportConstants.ConstantsFireBase.URL;
            URL url = new URL(json_url);
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

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.d(TAG, "onPostExecute call");

        String subject = ReportConstants.ConstantsFireBase.JSON_FIRE;
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Report" + "/" + subject + ".json");

        FileWriter fos = null;
        try {
            fos = new FileWriter(file);
            fos.write(result.toString());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
