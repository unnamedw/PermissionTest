package com.example.permissiontest

import org.junit.Test

import org.junit.Assert.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Base64

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun json_to_base64() {
        val encoded = android.util.Base64.encodeToString(sampleJson.toByteArray(), android.util.Base64.DEFAULT)
        println(encoded)
    }

    @Test
    fun dateTimeFormatterTest() {
        val input = "2022-04-21T11:40:00"
        val dateTime = LocalDateTime.parse(input, DateTimeFormatter.ISO_LOCAL_DATE_TIME)

        println(dateTime.dayOfWeek.name)
    }
}

