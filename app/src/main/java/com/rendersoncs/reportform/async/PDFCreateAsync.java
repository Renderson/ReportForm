package com.rendersoncs.reportform.async;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Message;
import android.util.Log;

import com.rendersoncs.reportform.itens.ReportItems;
import com.rendersoncs.reportform.util.CreatePDFViewer;

import java.io.File;

import static android.content.ContentValues.TAG;

public class PDFCreateAsync extends AsyncTask<ReportItems, Void, File> {
    private Context context;

    public PDFCreateAsync(Context context){
        this.context = context;
    }

    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG, "onPreExecute call");
    }

    @Override
    protected File doInBackground(ReportItems... reports) {
        Log.d(TAG, "doInBackground AWS call ");

        ReportItems report = reports[0];
        if (report == null) {
            cancel(true);
        }
        File pdfReport = null;
        try {
            pdfReport = new CreatePDFViewer().write(context, report);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pdfReport;
    }

    protected void onPostExecute(File file) {
        super.onPostExecute(file);
        Log.d(TAG, "onPostExecute call");
    }
}
