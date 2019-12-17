package com.rendersoncs.reportform.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.rendersoncs.reportform.R;
import com.rendersoncs.reportform.business.ReportBusiness;
import com.rendersoncs.reportform.itens.ReportItems;

public class ReportDataBaseAsyncTask extends AsyncTask<Void, Void, Void> {

    private PDFCreateAsync pdfCreateAsync;
    private Context context;
    private final int mReportId;
    private final ReportItems reportItems;
    private final ReportBusiness mReportBusiness;
    private final ReportDataBaseAsyncTask.FinishReport listener;
    private ProgressDialog progressDialog;

    public ReportDataBaseAsyncTask(Context context, PDFCreateAsync pdfCreateAsync, int mReportId1, ReportBusiness mReportBusiness, ReportItems reportItems, FinishReport listener) {
        this.context = context;
        this.pdfCreateAsync = pdfCreateAsync;
        this.mReportId = mReportId1;
        this.mReportBusiness = mReportBusiness;
        this.reportItems = reportItems;
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
