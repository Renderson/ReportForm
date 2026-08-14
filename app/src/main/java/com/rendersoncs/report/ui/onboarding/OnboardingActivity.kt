package com.rendersoncs.report.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.auth.FirebaseAuth
import com.rendersoncs.report.ui.ReportActivity
import com.rendersoncs.report.ui.login.AuthActivity
import com.rendersoncs.report.ui.theme.ReportTheme
import com.rendersoncs.report.ui.theme.ThemeSettings
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OnboardingActivity : ComponentActivity() {

    @Inject
    lateinit var themeSettings: ThemeSettings

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color.Transparent.toArgb(),
                Color.Transparent.toArgb()
            ),
            navigationBarStyle = SystemBarStyle.light(
                Color.Transparent.toArgb(),
                Color.Transparent.toArgb()
            )
        )
        super.onCreate(savedInstanceState)
        if (OnboardingPrefs.isCompleted(this)) {
            openNext()
            return
        }
        setContent {
            val darkTheme by themeSettings.darkTheme.collectAsStateWithLifecycle()
            ReportTheme(darkTheme = darkTheme) {
                OnboardingScreen(onFinished = ::completeOnboarding)
            }
        }
    }

    private fun completeOnboarding() {
        OnboardingPrefs.markCompleted(this)
        openNext()
    }

    private fun openNext() {
        val destination = if (FirebaseAuth.getInstance().currentUser != null) {
            ReportActivity::class.java
        } else {
            AuthActivity::class.java
        }
        startActivity(Intent(this, destination).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
        finish()
        overridePendingTransition(0, 0)
    }
}
