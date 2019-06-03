package com.rendersoncs.reportform.view;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import com.rendersoncs.reportform.itens.Repo;

import java.io.File;

import static android.content.ContentValues.TAG;

public class PDFAsyncTask extends AsyncTask<Repo, Void, File> {
    private Context context;
    private ProgressDialog progress;

    public PDFAsyncTask(Context context){
        this.context = context;
    }

    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG, "onPreExecute call");

        /*progress = new ProgressDialog(context);
        progress.setTitle("Por favor aguarde!");
        progress.setMessage("Carregando PDF...");
        progress.show();*/

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
        //this.onOpenPDFViewer(file);
        //progress.dismiss();
    }
}
