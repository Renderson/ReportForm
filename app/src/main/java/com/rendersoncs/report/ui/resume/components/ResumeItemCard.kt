package com.rendersoncs.report.ui.resume.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rendersoncs.report.R
import com.rendersoncs.report.common.constants.ReportConstants
import com.rendersoncs.report.model.ReportResumeItems
import com.rendersoncs.report.ui.theme.ConformGreen
import com.rendersoncs.report.ui.theme.NonConformRed
import com.rendersoncs.report.ui.theme.NotApplicable
import com.rendersoncs.report.ui.theme.ReportShapes

@Composable
fun ResumeItemCard(
    item: ReportResumeItems,
    onPhotoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stripeColor = when (item.conformity) {
        ReportConstants.ITEM.OPT_NUM1 -> ConformGreen
        ReportConstants.ITEM.OPT_NUM2 -> NotApplicable
        else -> NonConformRed
    }
    val statusLabel = when (item.conformity) {
        ReportConstants.ITEM.OPT_NUM1 -> stringResource(R.string.according)
        ReportConstants.ITEM.OPT_NUM2 -> stringResource(R.string.not_applicable)
        else -> stringResource(R.string.not_according)
    }
    val note = item.note.ifBlank { stringResource(R.string.label_not_observation) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = ReportShapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                Row {
                    ResumePhoto(
                        path = item.photo,
                        modifier = Modifier
                            .size(48.dp)
                            .clickable(onClick = onPhotoClick)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = note,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = statusLabel.uppercase(),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = stripeColor
                )
            }
        }
    }
}
