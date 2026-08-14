package com.rendersoncs.report.common.pdf

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.rendersoncs.report.R
import com.rendersoncs.report.common.util.ReportFiles
import com.rendersoncs.report.model.Report
import com.rendersoncs.report.model.ReportResumeItems
import com.rendersoncs.report.view.MyApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PDFGenerator {

    suspend fun generatePDF(
        report: Report,
        checkList: ArrayList<ReportResumeItems>
    ): Boolean = withContext(Dispatchers.IO) {
        val context = MyApplication.appContext
        try {
            val datePart = runCatching { report.dateFormatter() }.getOrElse {
                report.date.orEmpty().replace("/", "-").ifBlank { "sem-data" }
            }
            val companyPart = report.companyFormatter().ifBlank { "relatorio" }
            val fileName = context.getString(R.string.label_name_archive, companyPart, datePart)
                .replace(UNSAFE_FILE_CHARS, "-")
            val file = ReportFiles.pdfFile(context, fileName)
            if (file.exists()) file.delete()
            PdfCanvasWriter(context).write(report, checkList, file)
        } catch (error: Exception) {
            FirebaseCrashlytics.getInstance().recordException(error)
            false
        }
    }

    private companion object {
        val UNSAFE_FILE_CHARS = Regex("""[\\/:*?"<>|]""")
    }
}
