package com.rendersoncs.reportform.repository.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.rendersoncs.reportform.itens.ReportItems;

import java.util.ArrayList;
import java.util.List;

public class ReportRepository {

    private static ReportRepository INSTANCE;
    private ReportDataBaseHelper mReportDataBaseHelper;

    private ReportRepository(Context context) {
        if (context == null) {
            throw new NullPointerException();
        }
        this.mReportDataBaseHelper = new ReportDataBaseHelper(context);
    }

    public static ReportRepository getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new ReportRepository(context);
        }
        return INSTANCE;
    }

    public Boolean insert(ReportItems repo) {
        try {

            SQLiteDatabase sqLiteDatabase = this.mReportDataBaseHelper.getWritableDatabase();
            Log.i("log", "Item: " + sqLiteDatabase + " save DB");

            ContentValues insertValues = new ContentValues();
            insertValues.put(DataBaseConstants.REPORT.COLUMNS.COMPANY, repo.getCompany());
            insertValues.put(DataBaseConstants.REPORT.COLUMNS.EMAIL, repo.getEmail());
            insertValues.put(DataBaseConstants.REPORT.COLUMNS.CONTROLLER, repo.getController());
            insertValues.put(DataBaseConstants.REPORT.COLUMNS.SCORE, repo.getScore());
            insertValues.put(DataBaseConstants.REPORT.COLUMNS.DATE, repo.getDate());
            insertValues.put(DataBaseConstants.REPORT.COLUMNS.RESULT, repo.getResult());
            insertValues.put(DataBaseConstants.REPORT.COLUMNS.LIST, repo.getListJson());
            insertValues.put(DataBaseConstants.REPORT.COLUMNS.PHOTO, repo.getPhoto());
            Log.i("log", "Item: " + insertValues + " insertValues");

            sqLiteDatabase.insert(DataBaseConstants.REPORT.TABLE_NAME, null, insertValues);

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public Boolean update(ReportItems reportItems){
        try {

            SQLiteDatabase sqLiteDatabase = this.mReportDataBaseHelper.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put(DataBaseConstants.REPORT.COLUMNS.COMPANY, reportItems.getCompany());
            contentValues.put(DataBaseConstants.REPORT.COLUMNS.EMAIL, reportItems.getEmail());
            contentValues.put(DataBaseConstants.REPORT.COLUMNS.CONTROLLER, reportItems.getController());
            contentValues.put(DataBaseConstants.REPORT.COLUMNS.SCORE, reportItems.getScore());
            contentValues.put(DataBaseConstants.REPORT.COLUMNS.DATE, reportItems.getDate());
            contentValues.put(DataBaseConstants.REPORT.COLUMNS.RESULT, reportItems.getResult());
            contentValues.put(DataBaseConstants.REPORT.COLUMNS.LIST, reportItems.getListJson());
            contentValues.put(DataBaseConstants.REPORT.COLUMNS.PHOTO, reportItems.getPhoto());

            String selection = DataBaseConstants.REPORT.COLUMNS.ID + " = ?";
            String[] selectionArgs = {String.valueOf(reportItems.getId())};

            sqLiteDatabase.update(DataBaseConstants.REPORT.TABLE_NAME, contentValues, selection, selectionArgs);

            return true;

        } catch (Exception e){
            return false;
        }
    }

    public void remove(int id) {
        try {

            SQLiteDatabase sqLiteDatabase = this.mReportDataBaseHelper.getWritableDatabase();

            String whereClause = DataBaseConstants.REPORT.COLUMNS.ID + " = ?";
            String[] whereArgs = {String.valueOf(id)};

            sqLiteDatabase.delete(DataBaseConstants.REPORT.TABLE_NAME, whereClause, whereArgs);

        } catch (Exception e) {
            Log.i("TAG", "ERRORDB" + e.getMessage());
        }
    }

    public ReportItems load(int id) {
        ReportItems repoEntity = new ReportItems();

        try {

            SQLiteDatabase sqLiteDatabase = this.mReportDataBaseHelper.getReadableDatabase();

            String[] projection = {
                    DataBaseConstants.REPORT.COLUMNS.ID,
                    DataBaseConstants.REPORT.COLUMNS.COMPANY,
                    DataBaseConstants.REPORT.COLUMNS.EMAIL,
                    DataBaseConstants.REPORT.COLUMNS.CONTROLLER,
                    DataBaseConstants.REPORT.COLUMNS.SCORE,
                    DataBaseConstants.REPORT.COLUMNS.RESULT,
                    DataBaseConstants.REPORT.COLUMNS.LIST,
                    DataBaseConstants.REPORT.COLUMNS.DATE

            };

            String selection = DataBaseConstants.REPORT.COLUMNS.ID + " = ?";
            String[] selectionArgs = {String.valueOf(id)};

            Cursor cursor = sqLiteDatabase.query(DataBaseConstants.REPORT.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
            if (cursor != null && cursor.getCount() > 0){
                cursor.moveToFirst();
                repoEntity.setId(cursor.getInt(cursor.getColumnIndex(DataBaseConstants.REPORT.COLUMNS.ID)));
                repoEntity.setCompany(cursor.getString(cursor.getColumnIndex(DataBaseConstants.REPORT.COLUMNS.COMPANY)));
                repoEntity.setEmail(cursor.getString(cursor.getColumnIndex(DataBaseConstants.REPORT.COLUMNS.EMAIL)));
                repoEntity.setController(cursor.getString(cursor.getColumnIndex(DataBaseConstants.REPORT.COLUMNS.CONTROLLER)));
                repoEntity.setScore(cursor.getString(cursor.getColumnIndex(DataBaseConstants.REPORT.COLUMNS.SCORE)));
                repoEntity.setResult(cursor.getString(cursor.getColumnIndex(DataBaseConstants.REPORT.COLUMNS.RESULT)));
                repoEntity.setDate(cursor.getString(cursor.getColumnIndex(DataBaseConstants.REPORT.COLUMNS.DATE)));
                repoEntity.setListJson(cursor.getString(cursor.getColumnIndex(DataBaseConstants.REPORT.COLUMNS.LIST)));
            }

            if (cursor != null){
                cursor.close();
            }

            return repoEntity;

        } catch (Exception e) {
            return repoEntity;
        }
    }


    public List<ReportItems> getReportByQuery(String query) {
        List<ReportItems> list = new ArrayList<>();

        try {

            SQLiteDatabase sqLiteDatabase = this.mReportDataBaseHelper.getReadableDatabase();
            Cursor cursor = sqLiteDatabase.rawQuery(query, null);

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    ReportItems repoEntity = new ReportItems();
                    repoEntity.setId(cursor.getInt(cursor.getColumnIndex(DataBaseConstants.REPORT.COLUMNS.ID)));
                    repoEntity.setCompany(cursor.getString(cursor.getColumnIndex(DataBaseConstants.REPORT.COLUMNS.COMPANY)));
                    repoEntity.setEmail(cursor.getString(cursor.getColumnIndex(DataBaseConstants.REPORT.COLUMNS.EMAIL)));
                    repoEntity.setController(cursor.getString(cursor.getColumnIndex(DataBaseConstants.REPORT.COLUMNS.CONTROLLER)));
                    repoEntity.setScore(cursor.getString(cursor.getColumnIndex(DataBaseConstants.REPORT.COLUMNS.SCORE)));
                    repoEntity.setDate(cursor.getString(cursor.getColumnIndex(DataBaseConstants.REPORT.COLUMNS.DATE)));
                    repoEntity.setResult(cursor.getString(cursor.getColumnIndex(DataBaseConstants.REPORT.COLUMNS.RESULT)));
                    repoEntity.setListJson(cursor.getString(cursor.getColumnIndex(DataBaseConstants.REPORT.COLUMNS.LIST)));
                    //repoEntity.setPhoto(cursor.getBlob(cursor.getColumnIndex(DataBaseConstants.REPORT.COLUMNS.PHOTO)));

                    list.add(repoEntity);
                }
            }

            if (cursor != null) {
                cursor.close();
            }

        } catch (Exception e) {
            return list;
        }

        return list;
    }

    public void finalize() throws Throwable{
        if (null != mReportDataBaseHelper)
            mReportDataBaseHelper.close();
        super.finalize();
    }

    public void close(){
        if (null != mReportDataBaseHelper)
        mReportDataBaseHelper.close();
    }
}

