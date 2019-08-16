package com.rendersoncs.reportform.constants;

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

    public static class CONFIRMATION {

        public static final int CONFORM = 1;
        public static final int NOT_APPLICABLE = 2;
        public static final int NOT_CONFORM = 3;

    }
}
