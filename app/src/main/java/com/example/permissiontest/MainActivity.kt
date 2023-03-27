package com.example.permissiontest

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.permissiontest.databinding.ActivityMainBinding
import com.sangcomz.fishbun.FishBun
import com.sangcomz.fishbun.FishBun.Companion.INTENT_PATH
import com.sangcomz.fishbun.MimeType
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter
import java.security.Permission

class MainActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_ALBUM_MULTIPLE_REQUEST_CODE = 0
    }

    private lateinit var binding: ActivityMainBinding
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val toBeGrantedPermissions = listOf(android.Manifest.permission.READ_MEDIA_IMAGES)
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openGallery(1, REQUEST_ALBUM_MULTIPLE_REQUEST_CODE)
            } else {
                Toast.makeText(applicationContext, "권한 요청에 실패했어요", Toast.LENGTH_SHORT).show()
            }
//            results.forEach {
//                val permission = it.key
//                val isGranted = it.value
//
//                if (permission == android.Manifest.permission.READ_MEDIA_IMAGES && isGranted) {
//                    openGallery(1, REQUEST_ALBUM_MULTIPLE_REQUEST_CODE)
//                } else {
//                    Toast.makeText(applicationContext, "권한 요청에 실패했어요", Toast.LENGTH_SHORT).show()
//                }
//            }
        }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnPicker.setOnClickListener {
            if (checkSelfPermission(android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                openGallery(1, REQUEST_ALBUM_MULTIPLE_REQUEST_CODE)
            } else {
                requestPermissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_ALBUM_MULTIPLE_REQUEST_CODE -> if (resultCode == RESULT_OK) {
                // path = imageData.getStringArrayListExtra(Define.INTENT_PATH);
                // you can get an image path(ArrayList<String>) on <0.6.2
                val pathList = if (Build.VERSION.SDK_INT < 33) {
                    data?.getParcelableArrayListExtra(INTENT_PATH)
                } else {
                    data?.getParcelableArrayListExtra(INTENT_PATH, Uri::class.java)
                }

                binding.tvMessage.text = pathList.toString()
            }
        }
    }
}

fun Activity.openGallery(
    pickerCount: Int,
    requestCode: Int
) {
    FishBun.with(this)
        .setImageAdapter(GlideAdapter())
        .setIsUseDetailView(false)
        .setMaxCount(pickerCount)
        .setMinCount(1)
        .setPickerSpanCount(6)
        .setActionBarColor(Color.parseColor("#795548"), Color.parseColor("#5D4037"), false)
        .setActionBarTitleColor(Color.parseColor("#ffffff"))
        .setAlbumSpanCount(2, 4)
        .setButtonInAlbumActivity(false)
        .setCamera(true)
        .setReachLimitAutomaticClose(true)
        .setAllViewTitle("All")
        .setActionBarTitle("Image Library")
        .textOnImagesSelectionLimitReached("Limit Reached!")
        .textOnNothingSelected("Nothing Selected")
        .setSelectCircleStrokeColor(Color.BLACK)
        .isStartInAllView(false)
        .exceptMimeType(listOf(MimeType.GIF))
        .setSpecifyFolderList(arrayListOf("Screenshots", "Camera"))
        .startAlbumWithOnActivityResult(requestCode);
}