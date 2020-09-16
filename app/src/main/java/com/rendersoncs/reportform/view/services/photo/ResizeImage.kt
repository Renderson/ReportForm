/*

package com.rendersoncs.reportform.view.services.photo

import android.content.Context
import android.graphics.*
import android.media.ExifInterface
import android.util.Base64
import android.util.Log
import androidx.core.content.FileProvider
import com.crashlytics.android.Crashlytics
import com.facebook.FacebookSdk
import com.rendersoncs.reportform.BuildConfig
import com.rendersoncs.reportform.itens.ReportItems
import com.rendersoncs.reportform.view.adapter.ReportAdapter
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream


class ResizeImage {
    // Convert image Base64 String
    fun getEncoded64Image(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream)
        val byteFormat = stream.toByteArray()
        val imgString = Base64.encodeToString(byteFormat, Base64.DEFAULT)
        Log.d("Base64 ", imgString)
        return imgString
    }

    companion object {

        private const val NOT_ROTATE = 0
        private const val ROTATE_90 = 6
        private const val ROTATE_MINUS_90 = 7
        private const val WANTED_WIDTH = 640
        private const val WANTED_HEIGHT = 480

        private fun scaleBitmap(bitmap: Bitmap): Bitmap {
            val output = Bitmap.createBitmap(WANTED_WIDTH, WANTED_HEIGHT, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(output)
            val m = Matrix()
            m.setScale(WANTED_WIDTH.toFloat() / bitmap.width, WANTED_HEIGHT.toFloat() / bitmap.height)
            canvas.drawBitmap(bitmap, m, Paint())
            return output
        }

        private fun rotateBitmap(bitmap: Bitmap, orientation: Int): Bitmap? {
            val matrix = Matrix()
            when (orientation) {
                ExifInterface.ORIENTATION_NORMAL -> return bitmap
                ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.setScale(-1f, 1f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)
                ExifInterface.ORIENTATION_FLIP_VERTICAL -> {
                    matrix.setRotate(180f)
                    matrix.postScale(-1f, 1f)
                }
                ExifInterface.ORIENTATION_TRANSPOSE -> {
                    matrix.setRotate(90f)
                    matrix.postScale(-1f, 1f)
                }
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
                ExifInterface.ORIENTATION_TRANSVERSE -> {
                    matrix.setRotate(-90f)
                    matrix.postScale(-1f, 1f)
                }
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(-90f)
                else -> return bitmap
            }
            try {
                val bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                bitmap.recycle()
                return bmRotated
            } catch (e: OutOfMemoryError) {
                e.printStackTrace()
                return null
            }
        }

        @JvmStatic
        fun treatFile(context: Context, file: File, mAdapter: ReportAdapter, reportItems: ReportItems) {
            //val orientation = getOrientationFile(file)
            val uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".FileProvider", file)
            //mAdapter.setImageInItem(reportItems, uri)


            val bmOptions = BitmapFactory.Options()
            bmOptions.inJustDecodeBounds = true
            val cr = FacebookSdk.getApplicationContext().contentResolver
            val input: InputStream?
            try {
                input = cr.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(input)
                //val photo = rotateBitmap(bitmap, orientation)
                //mAdapter.setImageInItem(reportItems, photo)
                input?.close()
            } catch (e: Exception) {
                Crashlytics.logException(e)
            }

        }
    }
}*/
