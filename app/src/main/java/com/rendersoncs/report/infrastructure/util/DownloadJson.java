package com.rendersoncs.report.infrastructure.util;

import android.os.Environment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.core.Repo;
import com.rendersoncs.report.model.ReportItems;
import com.rendersoncs.report.view.login.util.User;
import com.rendersoncs.report.infrastructure.constants.ReportConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;

public class DownloadJson {

    public void addItemsFromJsonList(ArrayList<ReportItems> reportItemsList) {
        try {
            JSONObject js = new JSONObject(readJsonDataFromFile()).getJSONObject("list");

            Iterator<String> iterator = js.keys();
            while (iterator.hasNext()) {
                String dynamicKey = iterator.next();
                JSONObject jsKeys = js.getJSONObject(dynamicKey);

                String itemKey = jsKeys.getString(ReportConstants.ITEM.KEY);
                String itemTitle = jsKeys.getString(ReportConstants.ITEM.TITLE);
                String itemDescription = jsKeys.getString(ReportConstants.ITEM.DESCRIPTION);

                ReportItems reportItems = new ReportItems();
                reportItems.setKey(itemKey);
                reportItems.setTitle(itemTitle);
                reportItems.setDescription(itemDescription);
                reportItemsList.add(reportItems);

            }
        } catch (JSONException | IOException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }

    // Read Json and populate recyclerView
    private String readJsonDataFromFile() throws IOException {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        User user = new User();
        user.setId( mAuth.getCurrentUser().getUid() );

        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/" + "Report" + "/" + user.getId() + ".json";

        InputStream inputStream = null;
        StringBuilder builder = new StringBuilder();

        try {
            String jsonDataString;
            inputStream = new FileInputStream(path);
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            while ((jsonDataString = bufferedReader.readLine()) != null) {
                builder.append(jsonDataString);
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return new String(builder);
    }
}
