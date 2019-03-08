package com.rendersoncs.reportform.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.rendersoncs.reportform.constants.DataBaseConstants;
import com.rendersoncs.reportform.itens.Repo;

import java.util.ArrayList;
import java.util.List;

public class ReportRepository {

    private static ReportRepository INSTANCE;
    private ReportDataBaseHelper mReportDataBaseHelper;

    private ReportRepository(Context context){
        if (context == null){
            throw new NullPointerException();
        }
        this.mReportDataBaseHelper = new ReportDataBaseHelper(context);
    }

    public static ReportRepository getInstance(Context context){
        if (INSTANCE == null){
            INSTANCE = new ReportRepository(context);
        }
        return INSTANCE;
    }

    public Boolean insert(Repo repo){
        try{

            SQLiteDatabase sqLiteDatabase = this.mReportDataBaseHelper.getWritableDatabase();
            Log.i("log", "Item: " + sqLiteDatabase + " save DB");

            ContentValues insertValues = new ContentValues();
            insertValues.put(DataBaseConstants.REPORT.COLUMNS.COMPANY, repo.getCompany());
            insertValues.put(DataBaseConstants.REPORT.COLUMNS.EMAIL, repo.getEmail());
            insertValues.put(DataBaseConstants.REPORT.COLUMNS.DATE, repo.getDate());
            insertValues.put(DataBaseConstants.REPORT.COLUMNS.GRADES, repo.getConformed());
            insertValues.put(DataBaseConstants.REPORT.COLUMNS.LIST, repo.getListJson());
            insertValues.put(DataBaseConstants.REPORT.COLUMNS.PHOTO, repo.getPhoto());
            Log.i("log", "Item: " + insertValues + " insertValues");

            sqLiteDatabase.insert(DataBaseConstants.REPORT.TABLE_NAME, null, insertValues);

            return true;

        } catch (Exception e){
            return false;
        }
    }

    public Boolean remove(int id){
        try {

            SQLiteDatabase sqLiteDatabase = this.mReportDataBaseHelper.getWritableDatabase();

            String whereClause = DataBaseConstants.REPORT.COLUMNS.ID + " = ?";
            String[] whereArgs = {String.valueOf(id)};

            sqLiteDatabase.delete(DataBaseConstants.REPORT.TABLE_NAME, whereClause, whereArgs);

            return true;

        }catch (Exception e){
            return false;
        }
    }


    public List<Repo> getReportByQuery(String query){
        List<Repo> list = new ArrayList<>();

        try {

            SQLiteDatabase sqLiteDatabase = this.mReportDataBaseHelper.getReadableDatabase();
            Cursor cursor = sqLiteDatabase.rawQuery(query, null);

            if (cursor != null && cursor.getCount() > 0){
                while (cursor.moveToNext()){
                    Repo repoEntity = new Repo();
                    repoEntity.setId(cursor.getInt(cursor.getColumnIndex(DataBaseConstants.REPORT.COLUMNS.ID)));
                    repoEntity.setCompany(cursor.getString(cursor.getColumnIndex(DataBaseConstants.REPORT.COLUMNS.COMPANY)));
                    repoEntity.setEmail(cursor.getString(cursor.getColumnIndex(DataBaseConstants.REPORT.COLUMNS.EMAIL)));
                    repoEntity.setDate(cursor.getString(cursor.getColumnIndex(DataBaseConstants.REPORT.COLUMNS.DATE)));
                    repoEntity.setConformed(cursor.getInt(cursor.getColumnIndex(DataBaseConstants.REPORT.COLUMNS.GRADES)));
                    repoEntity.setListJson(cursor.getString(cursor.getColumnIndex(DataBaseConstants.REPORT.COLUMNS.LIST)));
                    //repoEntity.setPhoto(cursor.getBlob(cursor.getColumnIndex(DataBaseConstants.REPORT.COLUMNS.PHOTO)));

                    list.add(repoEntity);
                }
            }

            if (cursor != null){
                cursor.close();
            }

        } catch (Exception e){
            return list;
        }

        return list;
    }
}

