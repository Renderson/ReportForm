package com.rendersoncs.report.ui.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rendersoncs.report.R
import com.rendersoncs.report.model.Report
import com.rendersoncs.report.ui.theme.ReportShapes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableReportCard(
    report: Report,
    onOpen: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val canShare = report.concluded == true
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    onDelete()
                    false
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    if (canShare) onShare()
                    false
                }
                SwipeToDismissBoxValue.Settled -> false
            }
        },
        positionalThreshold = { distance -> distance * 0.3f }
    )

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier.clip(ReportShapes.medium),
        enableDismissFromStartToEnd = true,
        enableDismissFromEndToStart = canShare,
        backgroundContent = {
            Row(modifier = Modifier.fillMaxSize()) {
                SwipeActionBackground(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    color = MaterialTheme.colorScheme.error,
                    icon = Icons.Outlined.Delete,
                    iconTint = MaterialTheme.colorScheme.onError,
                    contentDescription = stringResource(R.string.alert_remove_report),
                    alignment = Alignment.CenterStart
                )
                SwipeActionBackground(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    color = if (canShare) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        Color.Transparent
                    },
                    icon = if (canShare) Icons.Outlined.Share else null,
                    iconTint = MaterialTheme.colorScheme.onPrimary,
                    contentDescription = stringResource(R.string.share),
                    alignment = Alignment.CenterEnd
                )
            }
        }
    ) {
        ReportListCard(
            report = report,
            onOpen = onOpen
        )
    }
}

@Composable
private fun SwipeActionBackground(
    color: Color,
    icon: ImageVector?,
    iconTint: Color,
    contentDescription: String,
    alignment: Alignment,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(color)
            .padding(horizontal = 24.dp),
        contentAlignment = alignment
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = iconTint
            )
        }
    }
}
