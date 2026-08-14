package com.rendersoncs.report.ui.resume

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rendersoncs.report.common.constants.ReportConstants
import com.rendersoncs.report.common.util.ReportFiles
import com.rendersoncs.report.model.Report
import com.rendersoncs.report.model.ReportCheckList
import com.rendersoncs.report.model.ReportResumeItems
import com.rendersoncs.report.repository.ReportRepository
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
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ResumeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val reportRepository: ReportRepository,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    private val reportId: Int = savedStateHandle.get<Long>(REPORT_ID_KEY)?.toInt() ?: 0

    private val _uiState = MutableStateFlow(ResumeUiState())
    val uiState: StateFlow<ResumeUiState> = _uiState.asStateFlow()

    private val eventsChannel = Channel<ResumeEvent>(Channel.BUFFERED)
    val events = eventsChannel.receiveAsFlow()

    init {
        load()
    }

    fun load() {
        if (reportId <= 0) {
            viewModelScope.launch { eventsChannel.send(ResumeEvent.LoadFailed) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = it.report == null) }
            try {
                val report = reportRepository.getReportById(reportId)
                val items = reportRepository.getReportWithChecklist(reportId.toString())
                    .flatMap { relation -> relation.checkList.map(::toResumeItem) }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        report = report,
                        items = items,
                        accordingCount = items.count { item -> item.conformity == ReportConstants.ITEM.OPT_NUM1 },
                        notApplicableCount = items.count { item -> item.conformity == ReportConstants.ITEM.OPT_NUM2 },
                        notAccordingCount = items.count { item -> item.conformity == ReportConstants.ITEM.OPT_NUM3 }
                    )
                }
            } catch (_: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                eventsChannel.send(ResumeEvent.LoadFailed)
            }
        }
    }

    fun openPdf() {
        val report = _uiState.value.report ?: return
        if (report.concluded != true) return
        viewModelScope.launch {
            val (subject, uri) = pdfDocument(report)
            if (subject == null || uri == null) {
                eventsChannel.send(ResumeEvent.PdfMissing)
            } else {
                eventsChannel.send(ResumeEvent.OpenPdf(uri, subject))
            }
        }
    }

    fun sharePdf() {
        val report = _uiState.value.report ?: return
        if (report.concluded != true) return
        viewModelScope.launch {
            val (subject, uri) = pdfDocument(report)
            if (subject == null || uri == null) {
                eventsChannel.send(ResumeEvent.PdfMissing)
            } else {
                eventsChannel.send(
                    ResumeEvent.SharePdf(
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

    fun deleteReport() {
        val report = _uiState.value.report ?: return
        viewModelScope.launch {
            deletePhotos(report)
            deletePdfFile(report)
            reportRepository.deleteReportByID(report.id ?: 0)
            eventsChannel.send(ResumeEvent.Deleted)
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

    private suspend fun deletePhotos(report: Report) {
        try {
            reportRepository.getReportWithChecklist(report.id.toString()).forEach { relation ->
                relation.checkList.forEach { item ->
                    val photo = item.photo
                    if (photo.isNotBlank() && photo != ReportConstants.PHOTO.NOT_PHOTO) {
                        File(photo).delete()
                    }
                }
            }
        } catch (_: Exception) {
            // Keep delete flow even if a photo file is already gone.
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

    private fun toResumeItem(item: ReportCheckList): ReportResumeItems {
        return ReportResumeItems(
            key = item.key,
            title = item.title,
            description = item.description,
            conformity = item.conformity,
            note = item.note,
            photo = item.photo
        )
    }

    private companion object {
        const val REPORT_ID_KEY = "reportId"
    }
}
