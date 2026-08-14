package com.rendersoncs.report.ui.camera

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rendersoncs.report.R
import com.rendersoncs.report.common.constants.ReportConstants
import com.rendersoncs.report.ui.theme.ReportTheme
import com.rendersoncs.report.ui.theme.ThemeSettings
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class CameraActivity : ComponentActivity() {

    @Inject
    lateinit var themeSettings: ThemeSettings

    private val viewModel: CameraViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.Transparent.toArgb()),
            navigationBarStyle = SystemBarStyle.dark(Color.Transparent.toArgb())
        )
        super.onCreate(savedInstanceState)
        setContent {
            val darkTheme by themeSettings.darkTheme.collectAsStateWithLifecycle()
            LaunchedEffect(Unit) {
                viewModel.events.collectLatest { event ->
                    when (event) {
                        is CameraEvent.Confirmed -> {
                            setResult(
                                ReportConstants.PHOTO.REQUEST_CAMERA_X,
                                Intent().putExtra(
                                    ReportConstants.PHOTO.RESULT_CAMERA_X,
                                    event.file
                                )
                            )
                            finish()
                        }
                        CameraEvent.CaptureFailed -> {
                            Toast.makeText(
                                this@CameraActivity,
                                getString(R.string.txt_error_save),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
            ReportTheme(darkTheme = darkTheme) {
                CameraScreen(
                    viewModel = viewModel,
                    onClose = ::finish
                )
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            viewModel.requestCapture()
            true
        } else {
            super.onKeyDown(keyCode, event)
        }
    }
}
