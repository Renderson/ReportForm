package com.rendersoncs.report.ui.login.util;

import android.content.Context;
import android.net.Uri;

public class User {
    private static final String PROVIDER = "com.rendersoncs.reportform.view.activitys.login.util.User.PROVIDER";

    private String id;
    private String name;
    private String email;
    private Uri photo;

    public User() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNameIfNull(String name) {
        if (this.name == null) {
            this.name = name;
        }
    }

    public String getEmail() {
        return email;
    }

    public Uri getPhoto() {
        return photo;
    }

    public void setEmailIfNull(String email) {
        if (this.email == null) {
            this.email = email;
        }
    }

    public void setUrlImgIfNull(Uri photoUrl) {
        if (this.photo == null) {
            this.photo = photoUrl;
        }
    }

    public void saveProviderSP(Context context, String token) {
        LibraryClass.saveSP(context, PROVIDER, token);
    }
}
