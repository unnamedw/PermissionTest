package com.example.permissiontest

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.BitmapCompat
import androidx.lifecycle.lifecycleScope
import com.example.permissiontest.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnPicker.setOnClickListener {
            pickImage()
        }
    }

    private fun handleUri(uri: Uri?) = lifecycleScope.launch {
        val file = FileManager.getFileFromUri(applicationContext, uri, ".png")
        val originFileSize = FileManager.getFileSize(file)
        binding.tvMessage.text = file?.name

        val imageBitmap = BitmapFactory.decodeFile(file?.absolutePath, BitmapFactory.Options().apply {})
//        val imageBitmapPng = compressBitmap(imageBitmap, Bitmap.CompressFormat.PNG, 100)

//        val newOutputStream = withContext(Dispatchers.IO) { FileOutputStream(file) }
//        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, newOutputStream)

        file?.also {
            FileManager.saveBitmapToPNGFile(imageBitmap, it)
        }

        binding.imageView.setImageBitmap(imageBitmap)
        binding.tvMessage.text = binding.tvMessage.text.toString()
            .plus("\nimage type >> ${FileManager.getImageType(file)}")
            .plus("\nfile origin size >> $originFileSize")
            .plus("\nfile size >> ${FileManager.getFileSize(file)}")

        file?.delete()
    }

    private suspend fun compressBitmap(bitmap: Bitmap, format: Bitmap.CompressFormat, quality: Int): Bitmap {
        return withContext(Dispatchers.Default) {
            val stream = ByteArrayOutputStream()
            bitmap.compress(format, quality, stream)
            val bytes = stream.toByteArray()
            BitmapFactory.decodeByteArray(stream.toByteArray(), 0, bytes.size)
        }
    }

    private fun pickImage() {
        // 파일을 가져오기 위해 ACTION_OPEN_DOCUMENT을 사용한다.
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT);
        // 이후 파일중 open가능한 것들로 카테고리를 묶기 위해 CATEGORY_OPENABLE을 사용한다.
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // 이제 Storage Access Framework에서 제공하는 UI에 노출될 MIME을 지정한다. 여기서는 이미지를 기준으로 작업하므로 image/라고 표기했지만
        // 오디오를 가지고 오고 싶다면 audio/를 사용하며 오디오 파일형식 중에서도 ogg파일만을 보고 싶다면 audio/ogg라고 명시한다.
        // 만약 모든 파일을 보고 싶다면 */*로 표기하면 된다.
        intent.type = "image/*"

        // 결과를 onActivityResult()로 전달받기 위해 startActivityForResult로 실행한다.
        startActivityForResult(intent, READ_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK) return

        when (requestCode) {
            READ_REQUEST_CODE -> {
                handleUri(data?.data)
            }
        }
    }

    companion object {
        private const val READ_REQUEST_CODE = 101
    }
}