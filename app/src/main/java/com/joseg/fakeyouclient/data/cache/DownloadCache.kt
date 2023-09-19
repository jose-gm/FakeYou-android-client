package com.joseg.fakeyouclient.data.cache

import android.content.Context
import android.util.Log
import com.joseg.fakeyouclient.common.utils.AlphanumericGenerator
import com.joseg.fakeyouclient.model.Audio
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.buffer
import okio.sink
import okio.source
import java.io.File
import javax.inject.Inject

// Reminder: see if this class still have any use
class DownloadCache @Inject constructor(
    private val context: Context,
    private val moshi: Moshi
) {
    private val directory = File(context.filesDir.path + "/downloadCache")
    private val jsonFile = File(directory.path + "/audio_download_dir_cache.json")

    init {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (!directory.exists()) {
                    directory.mkdirs()
                    if (directory.exists() && !jsonFile.exists())
                        jsonFile.createNewFile()
                }
            } catch (e: Throwable) {
                Log.e("downloadCache", e.stackTraceToString())
                jsonFile.delete()
            }
        }
    }

    suspend fun addAudio(audio: Audio) = withContext(Dispatchers.IO) {
        try {
            val type = Types.newParameterizedType(Map::class.java, String::class.java, String::class.java)
            val jsonAdapter: JsonAdapter<Map<String, String>> = moshi.adapter(type)
            var map: MutableMap<String, String>? = null

            try {
                jsonFile.inputStream().source().buffer().use {
                    map = jsonAdapter.fromJson(it)?.toMutableMap()
                    map?.putIfAbsent(AlphanumericGenerator.generate(15), AlphanumericGenerator.generate(15))
                }
            } catch (e: Exception) {
                Log.e("downloadCache", e.stackTraceToString())
            }

            jsonFile.outputStream().sink().buffer().use {
                it.write(
                    jsonAdapter
                        .toJson(
                        map ?: mutableMapOf(AlphanumericGenerator.generate(15) to AlphanumericGenerator.generate(15))
                    ).toByteArray()
                )

            }
        } catch (e: Throwable) {
            Log.e("downloadCache", e.stackTraceToString())
        }

    }
}