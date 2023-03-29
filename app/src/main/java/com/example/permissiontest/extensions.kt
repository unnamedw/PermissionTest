package com.example.permissiontest

import android.net.Uri
import android.util.Log
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

enum class PickType {
    IMAGE_AND_VIDEO,
    IMAGE,
    VIDEO,
    IMAGE_AND_VIDEO_INCLUDE_GIF
}