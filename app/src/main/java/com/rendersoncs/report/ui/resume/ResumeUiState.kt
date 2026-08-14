package com.rendersoncs.report.ui.resume

import android.net.Uri
import com.rendersoncs.report.model.Report
import com.rendersoncs.report.model.ReportResumeItems

data class ResumeUiState(
    val isLoading: Boolean = true,
    val report: Report? = null,
    val items: List<ReportResumeItems> = emptyList(),
    val accordingCount: Int = 0,
    val notApplicableCount: Int = 0,
    val notAccordingCount: Int = 0
) {
    val isConcluded: Boolean get() = report?.concluded == true
}

sealed interface ResumeEvent {
    data class OpenPdf(val uri: Uri, val subject: String) : ResumeEvent
    data class SharePdf(
        val uri: Uri,
        val subject: String,
        val email: String,
        val company: String,
        val date: String
    ) : ResumeEvent

    data object PdfMissing : ResumeEvent
    data object Deleted : ResumeEvent
    data object LoadFailed : ResumeEvent
}
