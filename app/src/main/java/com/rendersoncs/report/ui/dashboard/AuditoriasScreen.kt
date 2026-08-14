package com.rendersoncs.report.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AssignmentTurnedIn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rendersoncs.report.R
import com.rendersoncs.report.model.Report
import com.rendersoncs.report.ui.common.UiState
import com.rendersoncs.report.ui.dashboard.components.DashboardSearchBar
import com.rendersoncs.report.ui.dashboard.components.SearchFilterOption
import com.rendersoncs.report.ui.dashboard.components.ReportListCard

@Composable
fun AuditoriasScreen(
    state: DashboardUiState,
    onQueryChange: (String) -> Unit,
    onFilterChange: (AuditFilter) -> Unit,
    onOpenReport: (Report) -> Unit,
    listState: LazyListState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        DashboardSearchBar(
            query = state.query,
            onQueryChange = onQueryChange,
            selectedFilterId = state.filter.name,
            onFilterChange = { onFilterChange(AuditFilter.valueOf(it)) },
            filters = listOf(
                SearchFilterOption(
                    id = AuditFilter.ALL.name,
                    label = stringResource(R.string.dashboard_filter_all)
                ),
                SearchFilterOption(
                    id = AuditFilter.CONFORME.name,
                    label = stringResource(R.string.according)
                ),
                SearchFilterOption(
                    id = AuditFilter.NAO_CONFORME.name,
                    label = stringResource(R.string.not_according)
                ),
                SearchFilterOption(
                    id = AuditFilter.PENDENTE.name,
                    label = stringResource(R.string.dashboard_pending)
                )
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        when {
            state.listState is UiState.Loading && state.reports.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            state.reports.isEmpty() -> EmptyReports()
            state.visibleReports.isEmpty() -> EmptyReports(
                title = stringResource(R.string.dashboard_no_results),
                subtitle = stringResource(R.string.dashboard_no_results_hint)
            )
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = listState,
                    contentPadding = PaddingValues(bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.visibleReports, key = { it.id ?: it.hashCode() }) { report ->
                        ReportListCard(
                            report = report,
                            onOpen = { onOpenReport(report) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyReports(
    title: String = stringResource(R.string.label_nothing_report),
    subtitle: String = stringResource(R.string.label_create_new_report)
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.AssignmentTurnedIn,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
