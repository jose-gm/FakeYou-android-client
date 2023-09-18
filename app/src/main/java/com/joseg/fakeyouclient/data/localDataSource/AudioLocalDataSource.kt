package com.joseg.fakeyouclient.data.localDataSource

import com.joseg.fakeyouclient.model.Audio
import kotlinx.coroutines.flow.Flow

interface AudioLocalDataSource {
    suspend fun insert(audio: Audio)
    suspend fun delete(audio: Audio)
    suspend fun deleteAll()
    fun getAll(): Flow<List<Audio>>
}