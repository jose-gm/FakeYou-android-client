package com.joseg.fakeyouclient.data.download

import android.content.Context
import android.os.Environment
import com.joseg.fakeyouclient.R
import com.joseg.fakeyouclient.model.Audio
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val downloader: Downloader
) {
    private val _downloadQueue = mutableMapOf<String, Long>()

    fun downloadAudio(audio: Audio) {
        val id = downloader.downloadAudio(audio.url, audio.id)
        _downloadQueue[audio.id] = id
    }

    fun getDownloadProgress(audio: Audio): Flow<Int> = downloader.getDownloadProgress(_downloadQueue[audio.id] ?: 0L)
        .onCompletion {
            _downloadQueue.remove(audio.id)
        }

    fun getDownloadState(audio: Audio): DownloadState = downloader.getDownloadState(_downloadQueue[audio.id] ?: 0L)

    fun isAudioDownloaded(audio: Audio): Boolean {
        val file = File(
            context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)?.path +
            File.separator +
            context.getString(R.string.app_name) +
            File.separator +
            audio.id + ".wav"
        )

        return file.exists()
    }

    fun getAudioFilePath(audio: Audio): String? =
        if (isAudioDownloaded(audio))
            context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)?.path +
            File.separator +
            context.getString(R.string.app_name) +
            File.separator +
            audio.id + ".wav"
        else
            null

    fun deleteAudioFile(audio: Audio) {
        val file = File(
            context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)?.path +
                    File.separator +
                    context.getString(R.string.app_name) +
                    File.separator +
                    audio.id + ".wav"
        )

        if (getDownloadState(audio) == DownloadState.PENDING ||
            getDownloadState(audio) == DownloadState.DOWNLOADING ||
            getDownloadState(audio) == DownloadState.FAILED ||
            getDownloadState(audio) == DownloadState.PAUSED) {
            downloader.stopDownload(_downloadQueue[audio.id] ?: 0)
        } else {
            if (file.exists())
                file.delete()
        }
    }
}