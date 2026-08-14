package com.rendersoncs.report.ui.login

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.rendersoncs.report.ui.theme.ReportTheme

@Composable
fun AuthApp(
    darkTheme: Boolean,
    onAuthenticated: (String) -> Unit,
    onContactSupport: () -> Unit
) {
    ReportTheme(darkTheme = darkTheme) {
        AuthNavHost(
            onAuthenticated = onAuthenticated,
            onContactSupport = onContactSupport,
            modifier = Modifier.fillMaxSize()
        )
    }
}
