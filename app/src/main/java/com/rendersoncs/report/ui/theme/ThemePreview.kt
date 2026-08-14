package com.rendersoncs.report.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rendersoncs.report.ui.components.ReportOutlinedButton
import com.rendersoncs.report.ui.components.ReportPrimaryButton
import com.rendersoncs.report.ui.components.ReportSecondaryButton

@Preview(showBackground = true, name = "Stitch light")
@Composable
private fun ReportThemePreview() {
    ReportTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Headline", style = MaterialTheme.typography.headlineMedium)
                Text(
                    "Body e labels usam Inter no tema do app.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ReportPrimaryButton(text = "Primary", onClick = {})
                    ReportSecondaryButton(text = "Secondary", onClick = {})
                }
                ReportOutlinedButton(text = "Outlined", onClick = {})
            }
        }
    }
}
