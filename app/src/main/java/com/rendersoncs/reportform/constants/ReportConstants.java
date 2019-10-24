package com.rendersoncs.reportform.constants;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.rendersoncs.reportform.login.util.LibraryClass;
import com.rendersoncs.reportform.login.util.User;

public class ReportConstants {

    public static class ConstantsProvider{
        public static final String PACKAGE_FILE_PROVIDER = "com.rendersoncs.reportform.FileProvider";
    }

    public static class ConstantsFireBase{
        public static final String URL = "https://reportform-20b2a.firebaseio.com/users";
        public static final String JSON_FIRE = "reportApp";
        public static final String FIRE_DESCRIPTION = "description";
        public static final String FIRE_TITLE = "title";
    }

    public static class ConstantsBundle {
        public static final String REPORT_ID = "reportId";
    }

    public static class ConstantsCharacters{
        public static final int LIMITS_TEXT = 14;
    }

    public static class LIST_ITEMS {
        public static final String TITLE = "title";
        public static final String DESCRIPTION = "description";
        public static final String CONFORMITY = "conformity";
        public static final String NOTE = "note";
        public static final String PHOTO = "photo";
    }

    public  static  class TEST {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        String test = ReportConstants.ConstantsFireBase.URL + "/" + userId;

        public final String DB = test;

    }
}
