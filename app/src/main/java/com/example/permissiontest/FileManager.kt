package com.example.permissiontest

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.provider.OpenableColumns
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream

object FileManager {

    fun mkCacheFileFromUri(context: Context, uri: Uri?, suffix: String): File? {
        if (uri == null) {
            return null
        }

        var inputStream: InputStream? = null
        var fileOutputStream: FileOutputStream? = null

        try {
            inputStream = context.contentResolver.openInputStream(uri)
            val tmpFile = File.createTempFile(inputStream.hashCode().toString(), suffix)
            tmpFile.deleteOnExit()

            fileOutputStream = FileOutputStream(tmpFile)
            fileOutputStream.write(inputStream?.readBytes())

            return tmpFile
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        } finally {
            inputStream?.close()
            fileOutputStream?.close()
        }

    }

    suspend fun renameFile(file: File?, newName: String): File? = withContext(Dispatchers.IO){
        file ?: return@withContext null

        File(file.parent?.toString(), newName).also {
            file.renameTo(it)
            file.delete()
            return@withContext it
        }
    }

    fun getMimeType(file: File?): String? {
        file ?: return null
        val fileInputStream = FileInputStream(file)

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeStream(fileInputStream, null, options)

        val mimeType = options.outMimeType ?: return null
        return mimeType.split("/")[1]
    }

    suspend fun saveBitmapToPNGFile(bitmap: Bitmap, file: File?) = withContext(Dispatchers.IO) {
        if (file == null) {
            return@withContext
        }

        try {
            val exif = ExifInterface(file.absolutePath)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )

            val matrix = Matrix()
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            }

            val rotatedBitmap = Bitmap.createBitmap(
                bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
            )

            val out = FileOutputStream(file)
            rotatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getFileSize(file: File?): String? {
        val fileSize = file?.length()?.toDouble() ?: return null
        val num: Double
        val suffix: String

        if (fileSize > 1024*1024) {
            suffix = "MB"
            num = fileSize/(1024*1024)
        } else if (fileSize > 1024) {
            suffix = "KB"
            num = fileSize/1024
        } else {
            suffix = "B"
            num = fileSize
        }

        return "${Math.round(num * 100) / 100.0}$suffix"
    }

    fun getFileName(context: Context, uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    result = if (index == -1) {
                        throw IllegalArgumentException("Column index not found in query result.")
                    } else {
                        it.getString(index)
                    }
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != -1) {
                result = result?.substring(cut?.plus(1) ?: 0)
            }
        }
        return result ?: ""
    }

}