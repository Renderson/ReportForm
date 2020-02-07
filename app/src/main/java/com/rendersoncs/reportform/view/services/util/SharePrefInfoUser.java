package com.rendersoncs.reportform.view.services.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rendersoncs.reportform.view.services.constants.ReportConstants;

import static android.content.Context.MODE_PRIVATE;

public class SharePrefInfoUser {

    void saveUserSharePref(Context context, String user) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(ReportConstants.FIRE_BASE.FIRE_USERS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(ReportConstants.FIRE_BASE.FIRE_NAME, user);
        editor.apply();
    }

    void savePhotoSharePref(Context context, String photo) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(ReportConstants.FIRE_BASE.FIRE_USERS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(ReportConstants.FIRE_BASE.FIRE_PHOTO, photo);
        editor.apply();
    }

    void getUserSharePref(Context context, TextView profileName, ImageView profileView) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(ReportConstants.FIRE_BASE.FIRE_USERS, MODE_PRIVATE);

        String name = sharedPreferences.getString(ReportConstants.FIRE_BASE.FIRE_NAME, "");
        String photo = sharedPreferences.getString(ReportConstants.FIRE_BASE.FIRE_PHOTO, "");

        profileName.setText(name);
        Glide.with(context).load(photo).into(profileView);
    }

    public void clearSharePref(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(ReportConstants.FIRE_BASE.FIRE_USERS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit().clear();
        editor.apply();
    }
}
