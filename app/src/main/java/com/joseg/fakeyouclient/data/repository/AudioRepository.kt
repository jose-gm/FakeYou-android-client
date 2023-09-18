package com.joseg.fakeyouclient.data.repository

import com.joseg.fakeyouclient.data.localDataSource.AudioDbDataSource
import com.joseg.fakeyouclient.model.Audio
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AudioRepository @Inject constructor(
    private val audioDbDataSource: AudioDbDataSource
) {
    suspend fun insert(audio: Audio) = audioDbDataSource.insert(audio)
    suspend fun delete(audio: Audio) = audioDbDataSource.delete(audio)
    suspend fun deleteAll(audio: Audio) = audioDbDataSource.deleteAll()
    fun getAll(): Flow<List<Audio>> = audioDbDataSource.getAll()
}