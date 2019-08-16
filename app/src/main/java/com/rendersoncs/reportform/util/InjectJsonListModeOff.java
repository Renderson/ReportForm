package com.rendersoncs.reportform.util;

import android.os.Environment;
import com.google.firebase.auth.FirebaseAuth;
import com.rendersoncs.reportform.constants.ReportConstants;
import com.rendersoncs.reportform.itens.ReportItems;
import com.rendersoncs.reportform.login.util.User;

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

public class InjectJsonListModeOff {
    private FirebaseAuth mAuth;
    private User user;

    public void addItemsFromJsonList(ArrayList<ReportItems> reportItemsList) {
        try {
            JSONObject js = new JSONObject(readJsonDataFromFile()).getJSONObject("list");

            Iterator<String> iterator = js.keys();
            while (iterator.hasNext()) {
                String dynamicKey = iterator.next();
                JSONObject jsKeys = js.getJSONObject(dynamicKey);

                String itemTitle = jsKeys.getString(ReportConstants.ConstantsFireBase.FIRE_TITLE);
                String itemDescription = jsKeys.getString(ReportConstants.ConstantsFireBase.FIRE_DESCRIPTION);

                ReportItems reportItems = new ReportItems();
                reportItems.setTitle(itemTitle);
                reportItems.setDescription(itemDescription);
                reportItemsList.add(reportItems);

            }
        } catch (JSONException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Read Json and populate recyclerView
    private String readJsonDataFromFile() throws IOException {

        mAuth = FirebaseAuth.getInstance();
        user = new User();
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
