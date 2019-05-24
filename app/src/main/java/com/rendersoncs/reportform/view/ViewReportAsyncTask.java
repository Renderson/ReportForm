package com.rendersoncs.reportform.view;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.rendersoncs.reportform.business.ReportBusiness;
import com.rendersoncs.reportform.constants.ReportConstants;
import com.rendersoncs.reportform.itens.Repo;

import java.io.File;


public class ViewReportAsyncTask extends AppCompatActivity {
    private static final String TAG = "ViewReportAsyncTask";
    private static final int REQUEST_CODE_SHOWPDF = 1;

    private ReportBusiness mReportBusiness;
    private AsyncTask<Repo, Void, File> asyncTask = new ast();
    private Repo report = null;

    private ProgressDialog progress;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Camada Business
        this.mReportBusiness = new ReportBusiness(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int mReportId = bundle.getInt(ReportConstants.BundleConstants.REPORT_ID);

            report = this.mReportBusiness.load(mReportId);
        }

        if (savedInstanceState == null) {
            savedInstanceState = getIntent().getExtras();
        }
        onRestoreInstanceState(savedInstanceState);
        this.asyncTask.execute(this.report);
    }

    class ast extends AsyncTask<Repo, Void, File> {
        ast() {
        }

        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "onPreExecute call");

            progress = new ProgressDialog(ViewReportAsyncTask.this);
            progress.setTitle("Por favor aguarde!");
            progress.setMessage("Criando PDF...");
            progress.setMax(100);
            progress.show();

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
                pdfReport = new PDFReport().write(ViewReportAsyncTask.this, report);
            } catch (Exception e) {
                Log.i("Error", "Erro gerar PDF!!!!" + report + "pdfReport");
            }
            return pdfReport;
        }

        protected void onCancelled(File file) {
            super.onCancelled(file);
            ViewReportAsyncTask.this.handleError("Processo cancelado");
        }

        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            Log.d(TAG, "onPostExecute call");
            ViewReportAsyncTask.this.showPDFFile(file);
            progress.dismiss();
        }
    }

    protected void onStop() {
        super.onStop();
        if (this.asyncTask.getStatus() == AsyncTask.Status.RUNNING) {
            this.asyncTask.cancel(true);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        finish();
    }

    private void handleError(String message) {
        Toast.makeText(this, "Ops, tente novamente!", Toast.LENGTH_LONG).show();
        finish();
    }

    public void showPDFFile(File file) {
        try {
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                uri = FileProvider.getUriForFile(ViewReportAsyncTask.this, "com.rendersoncs.reportform.FileProvider", file);
            } else {
                uri = Uri.fromFile(file);
            }
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, REQUEST_CODE_SHOWPDF);

        } catch (ActivityNotFoundException e) {
            Toast.makeText(ViewReportAsyncTask.this, "Não encontrado aplicativo para LER PDF!", Toast.LENGTH_LONG).show();
        }
        Toast.makeText(ViewReportAsyncTask.this, "PDF Criado!", Toast.LENGTH_LONG).show();
    }
}
