package com.rendersoncs.reportform.repository.dao;

public class DataBaseConstants {

    private DataBaseConstants () {}

    public static class REPORT {
        public static final String TABLE_NAME = "Report";

        public  static class COLUMNS {
            static final String ID = "id";
            static final String COMPANY = "company";
            static final String EMAIL = "email";
            static final String CONTROLLER = "controller";
            static final String SCORE = "score";
            static final String DATE = "date";
            static final String RESULT = "result";
            static final String LIST = "jsonList";
            public static final String PHOTO = "photo";
        }
    }
}