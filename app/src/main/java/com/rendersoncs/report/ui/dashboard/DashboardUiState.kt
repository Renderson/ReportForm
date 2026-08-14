package com.rendersoncs.report.ui.dashboard

import android.net.Uri
import com.rendersoncs.report.model.Report
import com.rendersoncs.report.ui.common.UiState

enum class HomeTab {
    AUDITS,
    PROFILE
}

enum class AuditFilter {
    ALL,
    CONFORME,
    NAO_CONFORME,
    PENDENTE
}

enum class AuditStatus {
    CONFORME,
    NAO_CONFORME,
    PENDENTE
}

data class DashboardUiState(
    val listState: UiState<List<Report>> = UiState.Loading,
    val reports: List<Report> = emptyList(),
    val visibleReports: List<Report> = emptyList(),
    val query: String = "",
    val filter: AuditFilter = AuditFilter.ALL
)

sealed interface DashboardEvent {
    data class SharePdf(
        val uri: Uri,
        val subject: String,
        val email: String,
        val company: String,
        val date: String
    ) : DashboardEvent

    data object PdfMissing : DashboardEvent
}

fun Report.auditStatus(): AuditStatus {
    if (concluded == false) return AuditStatus.PENDENTE
    val value = result.orEmpty().trim()
    val isConform = value.equals("CONFORME", ignoreCase = true) ||
        value.equals("ACCORDING", ignoreCase = true)
    return if (isConform) AuditStatus.CONFORME else AuditStatus.NAO_CONFORME
}
