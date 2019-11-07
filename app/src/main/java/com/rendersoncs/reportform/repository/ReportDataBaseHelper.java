package com.rendersoncs.reportform.repository;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.rendersoncs.reportform.constants.DataBaseConstants;

class ReportDataBaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Report.db";

    private static final String SQL_CREATE_TABLE_REPORT =
            "create table " + DataBaseConstants.REPORT.TABLE_NAME + " ("
                    + DataBaseConstants.REPORT.COLUMNS.ID + " integer primary key autoincrement, "
                    + DataBaseConstants.REPORT.COLUMNS.COMPANY + " text, "
                    + DataBaseConstants.REPORT.COLUMNS.EMAIL + " text, "
                    + DataBaseConstants.REPORT.COLUMNS.DATE + " text, "
                    + DataBaseConstants.REPORT.COLUMNS.GRADES + " integer, "
                    + DataBaseConstants.REPORT.COLUMNS.LIST + " text, "
                    + DataBaseConstants.REPORT.COLUMNS.PHOTO + " blob);";

    private static final String DROP_TABLE_REPORT = "DROP TABLE IF EXISTS " + DataBaseConstants.REPORT.TABLE_NAME;
    //private static final String ALTER_TABLE_ADD_COLUMN = "ALTER COLUMN IF EXISTS " + DataBaseConstants.REPORT.TABLE_NAME;

    public ReportDataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_REPORT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE_REPORT);
        db.execSQL(SQL_CREATE_TABLE_REPORT);

    }
}
