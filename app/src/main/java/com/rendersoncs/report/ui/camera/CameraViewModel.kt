package com.rendersoncs.report.ui.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(CameraUiState())
    val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()

    private val eventsChannel = Channel<CameraEvent>(Channel.BUFFERED)
    val events = eventsChannel.receiveAsFlow()

    private val _captureRequests = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val captureRequests: SharedFlow<Unit> = _captureRequests.asSharedFlow()

    fun onCamerasAvailable(canSwitch: Boolean) {
        _uiState.update { it.copy(canSwitchCamera = canSwitch) }
    }

    fun switchCamera() {
        if (!_uiState.value.canSwitchCamera || _uiState.value.isCapturing) return
        _uiState.update { it.copy(useFrontCamera = !it.useFrontCamera) }
    }

    fun requestCapture() {
        if (_uiState.value.isCapturing || _uiState.value.capturedPhotoPath != null) return
        _captureRequests.tryEmit(Unit)
    }

    fun onCaptureStarted() {
        _uiState.update { it.copy(isCapturing = true, showFlash = true) }
        viewModelScope.launch {
            delay(FLASH_MS)
            _uiState.update { it.copy(showFlash = false) }
        }
    }

    fun onCaptureSuccess(file: File) {
        _uiState.update {
            it.copy(
                isCapturing = false,
                capturedPhotoPath = file.absolutePath
            )
        }
    }

    fun onCaptureError() {
        _uiState.update { it.copy(isCapturing = false, showFlash = false) }
        viewModelScope.launch { eventsChannel.send(CameraEvent.CaptureFailed) }
    }

    fun retake() {
        val path = _uiState.value.capturedPhotoPath
        _uiState.update { it.copy(capturedPhotoPath = null, isCapturing = false) }
        path?.let { File(it).takeIf(File::exists)?.delete() }
    }

    fun confirm() {
        val path = _uiState.value.capturedPhotoPath ?: return
        viewModelScope.launch {
            eventsChannel.send(CameraEvent.Confirmed(File(path)))
        }
    }

    private companion object {
        const val FLASH_MS = 80L
    }
}
