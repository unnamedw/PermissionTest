package com.example.permissiontest

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.permissiontest.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        clearCacheDir()

        Log.d("my_test", "density >> ${resources.displayMetrics.density}")

        binding.btnPicker.setOnClickListener {
            RewardSuccessConfirmDialog.newInstance(
                RewardSuccessConfirmDialog.ViewParam(
                    title = "미션완료!",
                    message = "코박캐시 보상",
                    reward = "100 CC 획득!"
                )
            ).apply {
                onConfirm = { Toast.makeText(context, "리워드 획득 완료!", Toast.LENGTH_SHORT).show() }
            }.show(supportFragmentManager, RewardSuccessConfirmDialog::class.simpleName)
        }
    }

    private fun clearCacheDir() {
        if (cacheDir.exists()) {
            cacheDir.listFiles()?.forEach { cacheFile ->
                cacheFile.delete()
            }
        }
    }

    private fun handleUri(uri: Uri?) = lifecycleScope.launch {
        val file = FileManager.mkTmpFileFromUri(applicationContext, uri)
        val originFileSize = FileManager.getFileSize(file)

        // before
        Glide.with(this@MainActivity).load(file).into(binding.ivBefore)
        binding.tvBefore.text = file?.name
            .plus("\nimage type >> ${FileManager.getMimeType(file)}")
            .plus("\nfile origin size >> $originFileSize")
            .plus("\nfile size >> ${FileManager.getFileSize(file)}")

        // convert to png
        val newFile = FileManager.getFileForUploadImageFormat(applicationContext, uri)

        // after
        Glide.with(this@MainActivity).load(newFile).into(binding.ivAfter)
        binding.tvAfter.text = newFile?.name
            .plus("\nimage type >> ${FileManager.getMimeType(newFile)}")
            .plus("\nfile origin size >> $originFileSize")
            .plus("\nfile size >> ${FileManager.getFileSize(newFile)}")

    }

    private fun pickImage() {
        // 파일을 가져오기 위해 ACTION_OPEN_DOCUMENT을 사용한다.
        val intent = Intent(Intent.ACTION_GET_CONTENT);
//        // 이후 파일중 open가능한 것들로 카테고리를 묶기 위해 CATEGORY_OPENABLE을 사용한다.
//        intent.addCategory(Intent.CATEGORY_OPENABLE);

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