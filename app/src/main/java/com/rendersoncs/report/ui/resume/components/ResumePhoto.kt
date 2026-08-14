package com.rendersoncs.report.ui.resume.components

import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BrokenImage
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.viewinterop.AndroidView
import com.bumptech.glide.Glide
import com.rendersoncs.report.common.constants.ReportConstants
import com.rendersoncs.report.ui.theme.ReportShapes

@Composable
fun ResumePhoto(
    modifier: Modifier = Modifier,
    path: String
) {
    val hasPhoto = path.isNotBlank() && path != ReportConstants.PHOTO.NOT_PHOTO
    Box(
        modifier = modifier
            .clip(ReportShapes.small)
            .background(MaterialTheme.colorScheme.secondaryContainer),
        contentAlignment = Alignment.Center
    ) {
        if (hasPhoto) {
            AndroidView(
                factory = { context ->
                    ImageView(context).apply {
                        scaleType = ImageView.ScaleType.CENTER_CROP
                        adjustViewBounds = false
                    }
                },
                modifier = Modifier.matchParentSize(),
                update = { view ->
                    Glide.with(view)
                        .load(path)
                        .centerCrop()
                        .into(view)
                }
            )
        } else {
            Icon(
                imageVector = Icons.Outlined.BrokenImage,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f)
            )
        }
    }
}
