package com.rendersoncs.reportform.async;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.rendersoncs.reportform.itens.Repo;
import com.rendersoncs.reportform.util.CreatePDFViewer;

import java.io.File;

import static android.content.ContentValues.TAG;

public class PDFAsyncTask extends AsyncTask<Repo, Void, File> {
    private Context context;

    public PDFAsyncTask(Context context){
        this.context = context;
    }

    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG, "onPreExecute call");
    }

    @Override
    protected File doInBackground(Repo... reports) {
        Log.d(TAG, "doInBackground AWS call ");

        Repo report = reports[0];
        if (report == null) {
            cancel(true);
        }
        File pdfReport = null;
        try {
            pdfReport = new CreatePDFViewer().write(context, report);
        } catch (Exception e) {
        }
        return pdfReport;
    }

    protected void onPostExecute(File file) {
        super.onPostExecute(file);
        Log.d(TAG, "onPostExecute call");
    }
}
