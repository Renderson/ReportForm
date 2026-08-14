package com.rendersoncs.report.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.rendersoncs.report.ui.navigation.ReportNavHost
import com.rendersoncs.report.ui.theme.ReportTheme

@Composable
fun ReportApp() {
    ReportTheme {
        val navController = rememberNavController()
        ReportNavHost(
            navController = navController,
            modifier = Modifier.fillMaxSize()
        )
    }
}
