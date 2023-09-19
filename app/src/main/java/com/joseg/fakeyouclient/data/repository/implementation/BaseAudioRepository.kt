package com.joseg.fakeyouclient.data.repository.implementation

import com.joseg.fakeyouclient.data.localDataSource.AudioDatabaseSource
import com.joseg.fakeyouclient.data.repository.AudioRepository
import com.joseg.fakeyouclient.model.Audio
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BaseAudioRepository @Inject constructor(
    private val audioDatabaseSource: AudioDatabaseSource
) : AudioRepository {
    override suspend fun insert(audio: Audio) = audioDatabaseSource.insert(audio)
    override suspend fun delete(audio: Audio) = audioDatabaseSource.delete(audio)
    override suspend fun deleteAll(audio: Audio) = audioDatabaseSource.deleteAll()
    override fun getAll(): Flow<List<Audio>> = audioDatabaseSource.getAll()
}