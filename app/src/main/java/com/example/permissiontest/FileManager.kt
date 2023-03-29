package com.example.permissiontest

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

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