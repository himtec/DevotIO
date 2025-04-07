package com.example.devotio.cache

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class AudioCacheManager(private val context: Context) {
    private val cacheDir = File(context.cacheDir, "audio_cache")
    private val client = OkHttpClient()

    init {
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
    }

    fun getAudioFile(audioUrl: String): Flow<File> = flow {
        val fileName = audioUrl.hashCode().toString()
        val file = File(cacheDir, fileName)

        if (file.exists()) {
            emit(file)
            return@flow
        }

        try {
            val request = Request.Builder()
                .url(audioUrl)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IOException("Failed to download audio: ${response.code}")
                }

                response.body?.byteStream()?.use { inputStream ->
                    FileOutputStream(file).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
            }

            emit(file)
        } catch (e: Exception) {
            Log.e("AudioCacheManager", "Error caching audio: ${e.message}")
            throw e
        }
    }.flowOn(Dispatchers.IO)

    fun clearCache() {
        cacheDir.listFiles()?.forEach { file ->
            file.delete()
        }
    }

    fun getCacheSize(): Long {
        return cacheDir.listFiles()?.sumOf { it.length() } ?: 0L
    }

    fun isAudioCached(audioUrl: String): Boolean {
        val fileName = audioUrl.hashCode().toString()
        return File(cacheDir, fileName).exists()
    }
} 