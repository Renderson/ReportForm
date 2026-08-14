package com.rendersoncs.report.ui.resume.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rendersoncs.report.R
import com.rendersoncs.report.ui.theme.ConformGreen
import com.rendersoncs.report.ui.theme.NonConformRed
import com.rendersoncs.report.ui.theme.NotApplicable

@Composable
fun ResumeDonutChart(
    modifier: Modifier = Modifier,
    accordingCount: Int,
    notApplicableCount: Int,
    notAccordingCount: Int,
    totalItems: Int
) {
    val slices = listOf(
        accordingCount to ConformGreen,
        notApplicableCount to NotApplicable,
        notAccordingCount to NonConformRed
    ).filter { it.first > 0 }
    val total = slices.sumOf { it.first }.coerceAtLeast(0)

    Box(
        modifier = modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 22.dp.toPx()
            val inset = strokeWidth / 2f
            val arcSize = Size(size.minDimension - strokeWidth, size.minDimension - strokeWidth)
            val topLeft = Offset(inset, inset)
            val gap = 3f

            if (total == 0) {
                drawArc(
                    color = NotApplicable.copy(alpha = 0.35f),
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                )
            } else {
                var start = -90f
                slices.forEach { (count, color) ->
                    val sweep = (count / total.toFloat()) * 360f
                    val visibleSweep = (sweep - gap).coerceAtLeast(1f)
                    drawArc(
                        color = color,
                        startAngle = start,
                        sweepAngle = visibleSweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                    )
                    start += sweep
                }
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.label_list_selected),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Text(
                text = totalItems.toString(),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
