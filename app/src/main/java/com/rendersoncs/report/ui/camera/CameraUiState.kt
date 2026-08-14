package com.rendersoncs.report.ui.camera

import java.io.File

data class CameraUiState(
    val useFrontCamera: Boolean = false,
    val canSwitchCamera: Boolean = false,
    val isCapturing: Boolean = false,
    val capturedPhotoPath: String? = null,
    val showFlash: Boolean = false
)

sealed interface CameraEvent {
    data class Confirmed(val file: File) : CameraEvent
    data object CaptureFailed : CameraEvent
}
