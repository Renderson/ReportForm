package com.rendersoncs.report.ui.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rendersoncs.report.R
import com.rendersoncs.report.model.Report
import com.rendersoncs.report.ui.dashboard.AuditStatus
import com.rendersoncs.report.ui.dashboard.auditStatus
import com.rendersoncs.report.ui.theme.ConformContainer
import com.rendersoncs.report.ui.theme.ConformGreen
import com.rendersoncs.report.ui.theme.NonConformContainer
import com.rendersoncs.report.ui.theme.NonConformRed
import com.rendersoncs.report.ui.theme.PendingContainer
import com.rendersoncs.report.ui.theme.PendingGray
import com.rendersoncs.report.ui.theme.ReportShapes

@Composable
fun ReportListCard  (
    report: Report,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val status = report.auditStatus()
    val stripeColor = when (status) {
        AuditStatus.CONFORME -> ConformGreen
        AuditStatus.NAO_CONFORME -> NonConformRed
        AuditStatus.PENDENTE -> PendingGray
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = ReportShapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onOpen
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(stripeColor)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                Text(
                    text = report.company.orEmpty().ifBlank { stringResource(R.string.title_report) },
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                MetaRow(
                    icon = Icons.Outlined.CalendarMonth,
                    text = report.date.orEmpty()
                )
                Spacer(modifier = Modifier.height(4.dp))
                MetaRow(
                    icon = Icons.Outlined.LocationOn,
                    text = report.controller.orEmpty().ifBlank { report.email.orEmpty() }
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusChip(
                        status = status,
                        label = report.result.orEmpty().ifBlank {
                            stringResource(R.string.dashboard_pending)
                        }
                    )
                    TextButton(onClick = onOpen) {
                        Text(
                            text = if (status == AuditStatus.PENDENTE) {
                                stringResource(R.string.dashboard_continue)
                            } else {
                                stringResource(R.string.dashboard_view_details)
                            },
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetaRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    if (text.isBlank()) return
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun StatusChip(status: AuditStatus, label: String) {
    val (container, content) = when (status) {
        AuditStatus.CONFORME -> ConformContainer to ConformGreen
        AuditStatus.NAO_CONFORME -> NonConformContainer to NonConformRed
        AuditStatus.PENDENTE -> PendingContainer to PendingGray
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(container)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = content
        )
    }
}
