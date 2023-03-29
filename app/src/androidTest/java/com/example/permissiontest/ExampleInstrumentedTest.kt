package com.example.permissiontest

import android.util.Base64
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import java.net.URLEncoder
import java.nio.charset.Charset

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun json_to_base64() {
        Base64.encodeToString(sampleJson.toByteArray(), Base64.DEFAULT).also {
            println(it)
        }
    }

    @Test
    fun json_to_base64_with_url_encode() {

    }

    private fun String.toBase64EncodedString(): String {
        return URLEncoder.encode(this)
    }

    private fun String.toUrlEncodedString(): String {
        return Base64.encodeToString(this.toByteArray(), Base64.DEFAULT)
    }
}