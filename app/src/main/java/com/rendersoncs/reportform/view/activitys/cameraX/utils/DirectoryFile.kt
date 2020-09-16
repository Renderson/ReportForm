package com.rendersoncs.reportform.view.activitys.cameraX.utils

import android.content.Context
import com.rendersoncs.reportform.R
import java.io.File

/** Use external media if it is available, our app's file directory otherwise */

fun getOutputDirectory(context: Context): File {
    val appContext = context.applicationContext
    val mediaDir = fileDirectory(context, appContext)
    return if (mediaDir != null && mediaDir.exists())
        mediaDir else appContext.filesDir
}

private fun fileDirectory(context: Context, appContext: Context): File? {
    return context.externalMediaDirs.firstOrNull()?.let {
        File(it, appContext.resources.getString(R.string.report_directory_img)).apply { mkdirs() }
    }
}