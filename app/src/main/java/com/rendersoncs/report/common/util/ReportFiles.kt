package com.rendersoncs.report.common.util

import android.content.Context
import android.net.Uri
import android.os.Environment
import java.io.File

object ReportFiles {
    private const val FOLDER = "Report"

    @JvmStatic
    fun documentsDir(context: Context): File {
        val dir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), FOLDER)
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    @JvmStatic
    fun pdfFile(context: Context, fileName: String): File = File(documentsDir(context), fileName)

    @JvmStatic
    fun checklistJson(context: Context, userId: String): File =
        File(documentsDir(context), "$userId.json")

    @JvmStatic
    fun photosDir(context: Context): File {
        val dir = File(documentsDir(context), "photos")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    @JvmStatic
    fun newPhotoFile(context: Context): File =
        File(photosDir(context), "photo_${System.currentTimeMillis()}.jpg")

    @JvmStatic
    fun copyUriToAppFile(context: Context, uri: Uri): File? {
        return try {
            val dest = File(documentsDir(context), "photo_${System.currentTimeMillis()}.jpg")
            context.contentResolver.openInputStream(uri)?.use { input ->
                dest.outputStream().use { output -> input.copyTo(output) }
            } ?: return null
            dest
        } catch (_: Exception) {
            null
        }
    }
}
