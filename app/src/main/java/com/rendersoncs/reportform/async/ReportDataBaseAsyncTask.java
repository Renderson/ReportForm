package com.rendersoncs.reportform.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.rendersoncs.reportform.R;
import com.rendersoncs.reportform.business.ReportBusiness;
import com.rendersoncs.reportform.itens.ReportItems;

public class ReportDataBaseAsyncTask extends AsyncTask<Void, Void, Void> {

    private Context context;
    private PDFCreateAsync pdfCreateAsync;
    private final int mReportId;
    private final ReportBusiness mReportBusiness;
    private final ReportItems reportItems;
    private FirebaseAnalytics mFireBaseAnalytics;
    private final ReportDataBaseAsyncTask.FinishReport listener;
    private ProgressDialog progressDialog;

    public ReportDataBaseAsyncTask(Context context,
                                   PDFCreateAsync pdfCreateAsync,
                                   int mReportId1,
                                   ReportBusiness mReportBusiness,
                                   ReportItems reportItems,
                                   FirebaseAnalytics mFireBaseAnalytics,
                                   FinishReport listener) {

        this.context = context;
        this.pdfCreateAsync = pdfCreateAsync;
        this.mReportId = mReportId1;
        this.mReportBusiness = mReportBusiness;
        this.reportItems = reportItems;
        this.mFireBaseAnalytics = mFireBaseAnalytics;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(context.getResources().getString(R.string.label_wait));
        progressDialog.setMessage(context.getResources().getString(R.string.label_saving_report));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (mReportId == 0) {
            Bundle bundle = new Bundle();
            bundle.putString("pdf_open", "pdf");
            mFireBaseAnalytics.logEvent("open_pdf_renderson", bundle);

            mReportBusiness.insert(reportItems);
        } else {
            reportItems.setId(mReportId);
            mReportBusiness.update(reportItems);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        progressDialog.dismiss();
        pdfCreateAsync.execute(reportItems);
        Toast.makeText(context, R.string.txt_report_save, Toast.LENGTH_SHORT).show();
        listener.finishReport();
    }

    public interface FinishReport {
        void finishReport();
    }
}
