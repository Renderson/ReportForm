package com.rendersoncs.reportform.view.services.async

import android.content.ContentValues
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.rendersoncs.reportform.itens.ReportItems
import com.rendersoncs.reportform.view.services.util.CreatePDFViewer
import java.io.File

class PDFCreateAsync(private val context: Context) : AsyncTask<ReportItems?, Void?, File?>() {
    override fun onPreExecute() {
        super.onPreExecute()
        Log.d(ContentValues.TAG, "onPreExecute call")
    }

    override fun doInBackground(vararg reports: ReportItems?): File? {
        Log.d(ContentValues.TAG, "doInBackground AWS call ")
        val report = reports[0]
        if (report == null) {
            cancel(true)
        }
        var pdfReport: File? = null
        try {
            pdfReport = CreatePDFViewer().write(context, report)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return pdfReport
    }

    override fun onPostExecute(file: File?) {
        super.onPostExecute(file)
        Log.d(ContentValues.TAG, "onPostExecute call")
    }

}