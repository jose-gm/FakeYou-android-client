package com.joseg.fakeyouclient.data.download

import android.content.Context
import com.joseg.fakeyouclient.model.Audio
import javax.inject.Inject

class DownloadManager @Inject constructor(
    private val context: Context,
    private val downloader: Downloader
) {
    fun downloadAudio(audio: Audio) {
        downloader.downloadAudio(audio)
    }
}