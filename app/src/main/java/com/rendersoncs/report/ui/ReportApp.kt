package com.rendersoncs.report.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.rendersoncs.report.ui.navigation.ReportNavHost
import com.rendersoncs.report.ui.theme.ReportTheme

@Composable
fun ReportApp(
    darkTheme: Boolean,
    onLoggedOut: () -> Unit
) {
    ReportTheme(darkTheme = darkTheme) {
        val navController = rememberNavController()
        ReportNavHost(
            navController = navController,
            onLoggedOut = onLoggedOut,
            modifier = Modifier.fillMaxSize()
        )
    }
}
