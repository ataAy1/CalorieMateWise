package com.app.utils

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ImageUtils {
    suspend fun downloadImage(imageUrl: String, context: Context): File? {
        val client = OkHttpClient()
        val request = Request.Builder().url(imageUrl).build()

        return withContext(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val inputStream = response.body?.byteStream()
                val file = File(context.cacheDir, "temp_image.jpg")
                val outputStream = FileOutputStream(file)
                inputStream?.copyTo(outputStream)
                outputStream.close()
                inputStream?.close()
                file
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }
    }
}
