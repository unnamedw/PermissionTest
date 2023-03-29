package com.example.permissiontest

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

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnPicker.setOnClickListener {
            pickMedia(PickType.IMAGE) {
                handleUri(it)
            }

        }
    }

    private fun handleUri(uri: Uri?) = lifecycleScope.launch {
        val file = FileManager.getFileFromUri(applicationContext, uri, ".tmp")
        binding.tvMessage.text = file?.name

        val imageBitmap = BitmapFactory.decodeFile(file?.absolutePath, BitmapFactory.Options().apply {})

        val imageBitmapPng = compressBitmap(imageBitmap, Bitmap.CompressFormat.PNG, 50)

        binding.imageView.setImageBitmap(imageBitmapPng)
        binding.tvMessage.text = binding.tvMessage.text.toString()
            .plus("\nfile size >> ${file?.length()}")
            .plus("\norigin bitmap size >> ${BitmapCompat.getAllocationByteCount(imageBitmap)}")
            .plus("\ncompressed bitmap size >> ${BitmapCompat.getAllocationByteCount(imageBitmapPng)}")

    }

    private suspend fun compressBitmap(bitmap: Bitmap, format: Bitmap.CompressFormat, quality: Int): Bitmap {
        return withContext(Dispatchers.Default) {
            val stream = ByteArrayOutputStream()
            bitmap.compress(format, quality, stream)
            val bytes = stream.toByteArray()
            BitmapFactory.decodeByteArray(stream.toByteArray(), 0, bytes.size)
        }
    }

    private var onPickSuccess: ((Uri?) -> Unit)? = null
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        onPickSuccess?.invoke(uri)
    }
    private fun pickMedia(
        type: PickType,
        onPick: (Uri?) -> Unit
    ) {
        onPickSuccess = onPick

        when (type) {
            PickType.IMAGE -> {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
            PickType.VIDEO -> {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
            }
            PickType.IMAGE_AND_VIDEO -> {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
            }
            PickType.IMAGE_AND_VIDEO_INCLUDE_GIF -> {
                val mimeType = "image/gif"
                pickMedia.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.SingleMimeType(
                            mimeType
                        )
                    )
                )
            }
        }

    }
}