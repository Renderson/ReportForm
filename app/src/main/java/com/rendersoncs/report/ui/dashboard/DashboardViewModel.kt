package com.rendersoncs.report.ui.dashboard

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rendersoncs.report.common.util.ReportFiles
import com.rendersoncs.report.model.Report
import com.rendersoncs.report.repository.AuthRepository
import com.rendersoncs.report.repository.ReportRepository
import com.rendersoncs.report.ui.common.UiState
import com.rendersoncs.report.util.ReportConstants.PACKAGE.FILE_PROVIDER
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val reportRepository: ReportRepository,
    private val authRepository: AuthRepository,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val eventsChannel = Channel<DashboardEvent>(Channel.BUFFERED)
    val events = eventsChannel.receiveAsFlow()

    init {
        loadReports()
    }

    fun loadReports() {
        val uid = authRepository.currentUid.orEmpty()
        viewModelScope.launch {
            if (_uiState.value.reports.isEmpty()) {
                _uiState.update { it.copy(listState = UiState.Loading) }
            }
            val reports = reportRepository.getReportsByUser(uid)
            val state = _uiState.value
            _uiState.update {
                it.copy(
                    reports = reports,
                    visibleReports = applyFilters(reports, state.query, state.filter),
                    listState = if (reports.isEmpty()) UiState.Empty else UiState.Success(reports)
                )
            }
        }
    }

    fun onQueryChange(value: String) {
        _uiState.update { state ->
            state.copy(
                query = value,
                visibleReports = applyFilters(state.reports, value, state.filter)
            )
        }
    }

    fun onFilterChange(filter: AuditFilter) {
        _uiState.update { state ->
            state.copy(
                filter = filter,
                visibleReports = applyFilters(state.reports, state.query, filter)
            )
        }
    }

    fun shareReport(report: Report) {
        if (report.concluded != true) return
        viewModelScope.launch {
            val (subject, uri) = pdfDocument(report)
            if (subject == null || uri == null) {
                eventsChannel.send(DashboardEvent.PdfMissing)
            } else {
                eventsChannel.send(
                    DashboardEvent.SharePdf(
                        uri = uri,
                        subject = subject,
                        email = report.email.orEmpty(),
                        company = report.company.orEmpty(),
                        date = report.date.orEmpty()
                    )
                )
            }
        }
    }

    fun deleteReport(report: Report) {
        val id = report.id ?: return
        viewModelScope.launch {
            reportRepository.deleteChecklistPhotos(id)
            deletePdfFile(report)
            reportRepository.deleteReportByID(id)
            loadReports()
        }
    }

    private fun applyFilters(
        reports: List<Report>,
        query: String,
        filter: AuditFilter
    ): List<Report> {
        val needle = query.trim()
        return reports.filter { report ->
            val matchesFilter = when (filter) {
                AuditFilter.ALL -> true
                AuditFilter.CONFORME -> report.auditStatus() == AuditStatus.CONFORME
                AuditFilter.NAO_CONFORME -> report.auditStatus() == AuditStatus.NAO_CONFORME
                AuditFilter.PENDENTE -> report.auditStatus() == AuditStatus.PENDENTE
            }
            val matchesQuery = needle.isEmpty() ||
                report.company.orEmpty().contains(needle, ignoreCase = true) ||
                report.controller.orEmpty().contains(needle, ignoreCase = true) ||
                report.date.orEmpty().contains(needle, ignoreCase = true)
            matchesFilter && matchesQuery
        }
    }

    private fun pdfDocument(report: Report): Pair<String?, Uri?> {
        return try {
            val subject = String.format(
                "Report-%s-%s",
                report.companyFormatter(),
                report.dateFormatter()
            )
            val file = ReportFiles.pdfFile(appContext, "$subject.pdf")
            if (!file.exists()) return Pair(subject, null)
            Pair(subject, FileProvider.getUriForFile(appContext, FILE_PROVIDER, file))
        } catch (_: Exception) {
            Pair(null, null)
        }
    }

    private fun deletePdfFile(report: Report) {
        try {
            val (subject, uri) = pdfDocument(report)
            if (uri != null && subject != null) {
                appContext.contentResolver.delete(uri, subject, null)
            }
        } catch (_: Exception) {
            // PDF may not exist for unfinished reports.
        }
    }
}
