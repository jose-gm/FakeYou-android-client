package com.joseg.fakeyouclient.data.repository

import com.joseg.fakeyouclient.model.Audio
import kotlinx.coroutines.flow.Flow

interface AudioRepository {
    suspend fun insert(audio: Audio)
    suspend fun delete(audio: Audio)
    suspend fun deleteAll()
    fun getAll(): Flow<List<Audio>>
}