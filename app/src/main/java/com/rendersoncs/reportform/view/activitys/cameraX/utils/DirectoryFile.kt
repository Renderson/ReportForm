package com.rendersoncs.reportform.view.activitys.cameraX.utils

import android.content.Context
import com.rendersoncs.reportform.R
import java.io.File
import java.util.*

/** Use external media if it is available, our app's file directory otherwise */

fun getOutputDirectory(context: Context): File {
    val appContext = context.applicationContext
    val mediaDir = fileDirectory(context, appContext)
    return if (mediaDir != null && mediaDir.exists())
        mediaDir else appContext.filesDir
}

private fun fileDirectory(context: Context, appContext: Context): File? {
    return context.externalMediaDirs.firstOrNull()?.let {
        File(it, appContext.resources.getString(R.string.app_name)).apply { mkdirs() }
    }
}

fun deleteDirectory(context: Context){
    val appContext = context.applicationContext
    val mediaDir = fileDirectory(context, appContext)
    delete(mediaDir)
}

private fun delete(fileDirectory: File?) {
    if (fileDirectory == null) {
        return
    } else if (fileDirectory.isDirectory) for (child in Objects.requireNonNull(fileDirectory.listFiles())) delete(child)
    val delete = fileDirectory.delete()
}