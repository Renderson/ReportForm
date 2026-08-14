package com.rendersoncs.report.ui.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Cameraswitch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.rendersoncs.report.R
import com.rendersoncs.report.common.util.ReportFiles
import com.rendersoncs.report.ui.theme.ReportShapes
import kotlinx.coroutines.flow.collectLatest
import java.io.File

@Composable
fun CameraScreen(
    modifier: Modifier = Modifier,
    viewModel: CameraViewModel,
    onClose: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    val hasPermission = remember {
        ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED
    }
    var permissionGranted by remember { mutableStateOf(hasPermission) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        permissionGranted = granted
        if (!granted) {
            Toast.makeText(
                context,
                context.getString(R.string.label_permission_camera_denied),
                Toast.LENGTH_LONG
            ).show()
            onClose()
        }
    }

    LaunchedEffect(Unit) {
        if (!permissionGranted) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    LaunchedEffect(imageCapture) {
        viewModel.captureRequests.collectLatest {
            capturePhoto(
                context = context,
                imageCapture = imageCapture,
                useFrontCamera = state.useFrontCamera,
                onStarted = viewModel::onCaptureStarted,
                onSuccess = viewModel::onCaptureSuccess,
                onError = viewModel::onCaptureError
            )
        }
    }

    BackHandler {
        if (state.capturedPhotoPath != null) {
            viewModel.retake()
        } else {
            onClose()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        when {
            !permissionGranted -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
            state.capturedPhotoPath != null -> {
                ReviewContent(
                    photoPath = state.capturedPhotoPath.orEmpty(),
                    onBack = viewModel::retake,
                    onRetake = viewModel::retake,
                    onConfirm = viewModel::confirm
                )
            }
            else -> {
                PreviewContent(
                    useFrontCamera = state.useFrontCamera,
                    canSwitchCamera = state.canSwitchCamera,
                    isCapturing = state.isCapturing,
                    showFlash = state.showFlash,
                    onBound = { capture, canSwitch ->
                        imageCapture = capture
                        viewModel.onCamerasAvailable(canSwitch)
                    },
                    onBack = onClose,
                    onSwitch = viewModel::switchCamera,
                    onCapture = viewModel::requestCapture
                )
            }
        }
    }
}

@Composable
private fun PreviewContent(
    modifier: Modifier = Modifier,
    useFrontCamera: Boolean,
    canSwitchCamera: Boolean,
    isCapturing: Boolean,
    showFlash: Boolean,
    onBound: (ImageCapture, Boolean) -> Unit,
    onBack: () -> Unit,
    onSwitch: () -> Unit,
    onCapture: () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        CameraPreview(
            useFrontCamera = useFrontCamera,
            onBound = onBound,
            modifier = Modifier.fillMaxSize()
        )
        AnimatedVisibility(
            visible = showFlash,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            )
        }
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .statusBarsPadding()
                .padding(12.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = stringResource(R.string.back),
                tint = Color.White
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 32.dp, vertical = 28.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.size(56.dp))
            ShutterButton(
                enabled = !isCapturing,
                onClick = onCapture
            )
            IconButton(
                onClick = onSwitch,
                enabled = canSwitchCamera && !isCapturing,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Cameraswitch,
                    contentDescription = stringResource(R.string.camera_switch_camera_button_alt),
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
private fun ShutterButton(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .size(84.dp)
            .clip(CircleShape)
            .border(4.dp, Color.White, CircleShape)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(if (enabled) Color.White else Color.White.copy(alpha = 0.4f))
        )
    }
}

@Composable
private fun ReviewContent(
    modifier: Modifier = Modifier,
    photoPath: String,
    onBack: () -> Unit,
    onRetake: () -> Unit,
    onConfirm: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .statusBarsPadding()
                .padding(12.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = stringResource(R.string.back),
                tint = Color.White
            )
        }
        CapturedPhoto(
            path = photoPath,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(ReportShapes.large)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onRetake,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = ReportShapes.small,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
            ) {
                Text(
                    text = stringResource(R.string.camera_retake),
                    style = MaterialTheme.typography.labelLarge
                )
            }
            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = ReportShapes.small,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = stringResource(R.string.camera_use_photo),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun CapturedPhoto(
    modifier: Modifier = Modifier,
    path: String
) {
    val bitmap = remember(path) {
        decodeSampledBitmap(path, 1080, 1920)?.asImageBitmap()
    }
    if (bitmap != null) {
        Image(
            bitmap = bitmap,
            contentDescription = stringResource(R.string.camera_check_button_alt),
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    } else {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color.White)
        }
    }
}

private fun capturePhoto(
    context: Context,
    imageCapture: ImageCapture?,
    useFrontCamera: Boolean,
    onStarted: () -> Unit,
    onSuccess: (File) -> Unit,
    onError: () -> Unit
) {
    val capture = imageCapture ?: return
    val photoFile = ReportFiles.newPhotoFile(context)
    onStarted()
    val metadata = ImageCapture.Metadata().apply {
        isReversedHorizontal = useFrontCamera
    }
    val output = ImageCapture.OutputFileOptions.Builder(photoFile)
        .setMetadata(metadata)
        .build()
    capture.takePicture(
        output,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val mimeType = MimeTypeMap.getSingleton()
                    .getMimeTypeFromExtension(photoFile.extension)
                MediaScannerConnection.scanFile(
                    context,
                    arrayOf(photoFile.absolutePath),
                    arrayOf(mimeType),
                    null
                )
                onSuccess(photoFile)
            }

            override fun onError(exception: ImageCaptureException) {
                FirebaseCrashlytics.getInstance().recordException(exception)
                photoFile.delete()
                onError()
            }
        }
    )
}

private fun decodeSampledBitmap(path: String, reqWidth: Int, reqHeight: Int) =
    BitmapFactory.Options().run {
        inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, this)
        inSampleSize = calculateInSampleSize(this, reqWidth, reqHeight)
        inJustDecodeBounds = false
        BitmapFactory.decodeFile(path, this)
    }

private fun calculateInSampleSize(
    options: BitmapFactory.Options,
    reqWidth: Int,
    reqHeight: Int
): Int {
    val (height, width) = options.outHeight to options.outWidth
    var inSampleSize = 1
    if (height > reqHeight || width > reqWidth) {
        var halfHeight = height / 2
        var halfWidth = width / 2
        while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
            inSampleSize *= 2
        }
    }
    return inSampleSize
}
