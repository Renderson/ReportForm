package com.rendersoncs.report.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rendersoncs.report.model.Report
import com.rendersoncs.report.repository.AuthRepository
import com.rendersoncs.report.repository.ReportRepository
import com.rendersoncs.report.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val reportRepository: ReportRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

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
}
