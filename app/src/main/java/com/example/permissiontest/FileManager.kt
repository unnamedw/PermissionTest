package com.example.permissiontest

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
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
import kotlin.math.roundToInt

object FileManager {

    fun getFileFromUri(context: Context, uri: Uri?, suffix: String): File? {
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

    fun getFileType(file: File?): String? {
        if (file == null) {
            return null
        }

        val fileSignature = getFileSignature(file)

        // JPEG 파일인 경우
        if (fileSignature == "FFD8FF") {
            return "JPEG"
        }

        // PNG 파일인 경우
        if (fileSignature == "89504E47") {
            return "PNG"
        }

        // GIF 파일인 경우
        if (fileSignature == "47494638") {
            return "GIF"
        }

        // BMP 파일인 경우
        if (fileSignature == "424D") {
            return "BMP"
        }

        // 파일 형식을 알 수 없는 경우
        return null
    }

    private fun getFileSignature(file: File): String? {
        return try {
            val inputStream = FileInputStream(file)
            val buffer = ByteArray(8)
            inputStream.read(buffer, 0, 8)
            inputStream.close()

            buffer.joinToString("") { "%02X".format(it) }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getImageType(file: File?): String? {
        file ?: return null
        val fileInputStream = FileInputStream(file)

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeStream(fileInputStream, null, options)

        val mimeType = options.outMimeType ?: return null
        return mimeType.split("/")[1]
    }

    suspend fun saveBitmapToPNGFile(bitmap: Bitmap, file: File) = withContext(Dispatchers.Default) {
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

    @SuppressLint("Range")
    fun getFileNameFromUri(context: Context, uri: Uri?): String? {
        if (uri == null) {
            return null
        }

        var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } catch (e: Exception) {
                return null
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }
}