package com.rendersoncs.report.infrastructure.pdf

import android.content.ContentValues
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.rendersoncs.report.model.ReportItems
import java.io.File

class AsyncCreatePDF(private val context: Context) : AsyncTask<ReportItems?, Void?, File?>() {
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
            pdfReport = ReportAssemblePDF().write(context, report)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
        return pdfReport
    }

    override fun onPostExecute(file: File?) {
        super.onPostExecute(file)
        Log.d(ContentValues.TAG, "onPostExecute call")
    }

}