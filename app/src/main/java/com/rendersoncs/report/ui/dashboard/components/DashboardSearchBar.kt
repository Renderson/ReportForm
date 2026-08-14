package com.rendersoncs.report.ui.dashboard.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rendersoncs.report.R
import com.rendersoncs.report.ui.dashboard.AuditFilter
import com.rendersoncs.report.ui.theme.ReportShapes

@Composable
fun DashboardSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    selectedFilter: AuditFilter,
    onFilterChange: (AuditFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    var filterExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            placeholder = {
                Text(
                    text = stringResource(R.string.dashboard_search_hint),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            singleLine = true,
            shape = ReportShapes.small,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = MaterialTheme.colorScheme.outline,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Box {
            Surface(
                shape = ReportShapes.small,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 1.dp,
                shadowElevation = 1.dp
            ) {
                IconButton(
                    onClick = { filterExpanded = true },
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FilterList,
                        contentDescription = stringResource(R.string.dashboard_filter),
                        tint = if (selectedFilter == AuditFilter.ALL) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.secondary
                        }
                    )
                }
            }
            DropdownMenu(
                expanded = filterExpanded,
                onDismissRequest = { filterExpanded = false }
            ) {
                AuditFilter.entries.forEach { filter ->
                    DropdownMenuItem(
                        text = { Text(filterLabel(filter)) },
                        onClick = {
                            onFilterChange(filter)
                            filterExpanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun filterLabel(filter: AuditFilter): String {
    return when (filter) {
        AuditFilter.ALL -> stringResource(R.string.dashboard_filter_all)
        AuditFilter.CONFORME -> stringResource(R.string.according)
        AuditFilter.NAO_CONFORME -> stringResource(R.string.not_according)
        AuditFilter.PENDENTE -> stringResource(R.string.dashboard_pending)
    }
}
