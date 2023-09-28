package com.joseg.fakeyouclient.data.download

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import com.joseg.fakeyouclient.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import java.io.File
import javax.inject.Inject

interface Downloader {
    fun downloadAudio(url: String, fileName: String): Long
    fun getDownloadProgress(id: Long): Flow<Int>
    fun getDownloadState(id: Long): DownloadState
    fun stopDownload(id: Long)
}

enum class DownloadState {
    PENDING,
    DOWNLOADING,
    COMPLETED,
    PAUSED,
    FAILED,
    UNDEFINED;
}

class AndroidDownloader @Inject constructor(
    @ApplicationContext private val context: Context,
    private val ioDispatcher: CoroutineDispatcher
) : Downloader {
    private val downloadManager: DownloadManager = context.getSystemService(DownloadManager::class.java)

    override fun downloadAudio(url: String, filaName: String): Long {
        val appFolderName = context.getString(R.string.app_name)
        val audioFileName = "$filaName.wav"
        val request = DownloadManager.Request(Uri.parse(url))
            .setMimeType("audio/wav")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN)
            .setDestinationInExternalFilesDir(
                context,
                Environment.DIRECTORY_MUSIC,
                appFolderName + File.separator + audioFileName
            )
        return downloadManager.enqueue(request)
    }

    override fun getDownloadProgress(id: Long): Flow<Int> = flow {
        while (currentCoroutineContext().isActive) {
            val cursor = downloadManager.query(DownloadManager.Query().setFilterById(id))
            if (cursor.moveToFirst()) {
                val downloadedBytesIndex = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
                val totalBytesIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
                when (getDownloadState(id)) {
                    DownloadState.PENDING,
                    DownloadState.PAUSED -> true
                    DownloadState.DOWNLOADING -> {
                        val totalBytes = cursor.getDouble(totalBytesIndex)
                        val downloadedBytes = cursor.getDouble(downloadedBytesIndex)
                        if (totalBytes >= 0) {
                            val progress = ((downloadedBytes / totalBytes) * 100).toInt()
                            emit(progress)
                        }
                    }
                    DownloadState.COMPLETED,
                    DownloadState.FAILED,
                    DownloadState.UNDEFINED -> break
                }
            }
            cursor.close()
        }
    }
        .distinctUntilChanged()
        .flowOn(ioDispatcher)

    override fun getDownloadState(id: Long): DownloadState {
        val cursor = downloadManager.query(DownloadManager.Query().setFilterById(id))
        if (cursor.moveToFirst()) {
            val statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
            return when (cursor.getInt(statusIndex)) {
                DownloadManager.STATUS_SUCCESSFUL -> {
                    cursor.close()
                    DownloadState.COMPLETED
                }
                DownloadManager.STATUS_FAILED -> {
                    cursor.close()
                    DownloadState.FAILED
                }
                DownloadManager.STATUS_PAUSED -> {
                    cursor.close()
                    DownloadState.PAUSED
                }
                DownloadManager.STATUS_RUNNING -> {
                    cursor.close()
                    DownloadState.DOWNLOADING
                }
                DownloadManager.STATUS_PENDING -> {
                    cursor.close()
                    DownloadState.PENDING
                }
                else -> {
                    cursor.close()
                    DownloadState.UNDEFINED
                }
            }
        } else {
            cursor.close()
            return DownloadState.UNDEFINED
        }
    }

    override fun stopDownload(id: Long) {
        downloadManager.remove(id)
    }
}