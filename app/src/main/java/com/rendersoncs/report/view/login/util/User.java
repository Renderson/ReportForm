package com.rendersoncs.report.view.login.util;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.ValueEventListener;
import com.rendersoncs.report.common.constants.ReportConstants;

import java.util.HashMap;
import java.util.Map;

public class User {
    private static String PROVIDER = "com.rendersoncs.reportform.view.activitys.login.util.User.PROVIDER";

    private String id;
    private String name;
    private String email;

    private Uri photo;
    private String password;
    private String newPassword;

    public User(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Check user connected in MainActivity Menu
    public boolean isSocialNetworkLogged(Context context){
        String token = getProviderSP( context );
        return (token.contains("facebook") || token.contains("google") || token.contains("twitter"));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private void setNameInMap(Map<String, Object> map) {
        if (getName() != null){
            map.put("name", getName());
        }
    }

    public void setNameIfNull(String name) {
        if( this.name == null ){
            this.name = name;
        }
    }

    public String getEmail() {
        return email;
    }

    public Uri getPhoto() { return photo; }

    public void setEmail(String email) {
        this.email = email;
    }

    private void setEmailInMap(Map<String, Object> map) {
        if (getEmail() != null){
            map.put("email", getEmail());
        }
    }

    public void setEmailIfNull(String email) {
        if( this.email == null ){
            this.email = email;
        }
    }

    public void setUrlImgIfNull(Uri photoUrl) {
        if(this.photo == null) {
            this.photo = photoUrl;
        }
    }

    @Exclude
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public void saveProviderSP(Context context, String token){
        LibraryClass.saveSP(context, PROVIDER, token);
    }

    private String getProviderSP(Context context){
        return (LibraryClass.getSP(context, PROVIDER));
    }

    public void saveDB(DatabaseReference.CompletionListener... completionListener){
        DatabaseReference fireBase = LibraryClass.getFirebase().child(ReportConstants.FIREBASE.FIRE_USERS).child( getId() ).child(ReportConstants.FIREBASE.FIRE_CREDENTIAL);
        Log.i("LOG", "Tokens: " + fireBase);

        if( completionListener.length == 0 ){
            fireBase.setValue(this);
        }
        else{
            fireBase.setValue(this, completionListener[0]);
        }
    }

    public void updateDB( DatabaseReference.CompletionListener... completionListener ){

        DatabaseReference fireBase = LibraryClass.getFirebase().child(ReportConstants.FIREBASE.FIRE_USERS).child( getId() ).child(ReportConstants.FIREBASE.FIRE_CREDENTIAL);

        Map<String, Object> map = new HashMap<>();
        setNameInMap(map);
        setEmailInMap(map);

        if( map.isEmpty() ){
            return;
        }

        if( completionListener.length > 0 ){
            fireBase.updateChildren(map, completionListener[0]);
        }
        else{
            fireBase.updateChildren(map);
        }
    }

    public void removeDB( DatabaseReference.CompletionListener completionListener ){
        DatabaseReference fireBase = LibraryClass.getFirebase().child("users").child( getId() );
        fireBase.setValue(null, completionListener);
    }

    public void contextDataDB( Context context ){
        DatabaseReference fireBase = LibraryClass.getFirebase().child(ReportConstants.FIREBASE.FIRE_USERS).child( getId() ).child(ReportConstants.FIREBASE.FIRE_CREDENTIAL);
        fireBase.addListenerForSingleValueEvent( (ValueEventListener) context );
    }
}
