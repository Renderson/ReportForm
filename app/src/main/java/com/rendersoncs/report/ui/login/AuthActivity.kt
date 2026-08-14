package com.rendersoncs.report.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.auth.FirebaseAuth
import com.rendersoncs.report.ui.ReportActivity
import com.rendersoncs.report.ui.theme.ThemeSettings
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AuthActivity : ComponentActivity() {

    @Inject
    lateinit var themeSettings: ThemeSettings

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        if (FirebaseAuth.getInstance().currentUser != null) {
            openMain()
            return
        }
        setContent {
            val darkTheme by themeSettings.darkTheme.collectAsStateWithLifecycle()
            AuthApp(
                darkTheme = darkTheme,
                onAuthenticated = { openMain() },
                onContactSupport = { }
            )
        }
    }

    private fun openMain() {
        startActivity(Intent(this, ReportActivity::class.java))
        finish()
    }
}
