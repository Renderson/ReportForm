package com.rendersoncs.reportform.view.services.util;

import android.os.Environment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.rendersoncs.reportform.itens.ReportItems;
import com.rendersoncs.reportform.view.activitys.login.util.User;
import com.rendersoncs.reportform.view.services.constants.ReportConstants;

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

public class ListJsonOff {

    public void addItemsFromJsonList(ArrayList<ReportItems> reportItemsList) {
        try {
            JSONObject js = new JSONObject(readJsonDataFromFile()).getJSONObject("list");

            Iterator<String> iterator = js.keys();
            while (iterator.hasNext()) {
                String dynamicKey = iterator.next();
                JSONObject jsKeys = js.getJSONObject(dynamicKey);

                String itemTitle = jsKeys.getString(ReportConstants.ITEM.TITLE);
                String itemDescription = jsKeys.getString(ReportConstants.ITEM.DESCRIPTION);

                ReportItems reportItems = new ReportItems();
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
