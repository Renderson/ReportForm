package com.rendersoncs.report.view.login.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public final class LibraryClass {

    private static final String PREF = "com.rendersoncs.reportform.PREF";
    private static DatabaseReference firebase;

    private LibraryClass(){}

    public static DatabaseReference getFirebase(){
        if( firebase == null ){
            firebase = FirebaseDatabase.getInstance().getReference();
        }

        return( firebase );
    }

    static public void saveSP(Context context, String key, String value){
        SharedPreferences sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        sp.edit().putString(key, value).apply();
    }

    static public String getSP(Context context, String key){
        SharedPreferences sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        return (sp.getString(key, ""));
    }

    public static DatabaseReference closeFireBase(){
        firebase.onDisconnect();
        return null;
    }
}
