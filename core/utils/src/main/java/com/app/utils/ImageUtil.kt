package com.app.utils

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.ByteArrayOutputStream
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ImageUtil {
    private val client = OkHttpClient()

    suspend fun downloadImage(imageUrl: String, context: Context): ByteArray? {
        val request = Request.Builder().url(imageUrl).build()

        return withContext(Dispatchers.IO) {
            var response: Response? = null
            try {
                response = client.newCall(request).execute()
                if (!response.isSuccessful) {
                    return@withContext null
                }

                val inputStream = response.body?.byteStream()
                if (inputStream == null) {
                    return@withContext null
                }

                val byteArrayOutputStream = ByteArrayOutputStream()
                inputStream.copyTo(byteArrayOutputStream)
                byteArrayOutputStream.toByteArray()
            } catch (e: IOException) {
                e.printStackTrace()
                null
            } finally {
                response?.close()
            }
        }
    }
}
