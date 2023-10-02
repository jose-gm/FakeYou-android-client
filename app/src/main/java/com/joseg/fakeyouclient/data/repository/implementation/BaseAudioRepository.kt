package com.joseg.fakeyouclient.data.repository.implementation

import com.joseg.fakeyouclient.data.localDataSource.AudioLocalDataSource
import com.joseg.fakeyouclient.data.repository.AudioRepository
import com.joseg.fakeyouclient.model.Audio
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BaseAudioRepository @Inject constructor(
    private val audioDatabaseSource: AudioLocalDataSource,
    private val ioDispatcher: CoroutineDispatcher
) : AudioRepository {
    override suspend fun insert(audio: Audio) = withContext(ioDispatcher) { audioDatabaseSource.insert(audio) }
    override suspend fun delete(audios: List<Audio>) = withContext(ioDispatcher) { audioDatabaseSource.delete(audios) }
    override suspend fun deleteAll() = withContext(ioDispatcher) { audioDatabaseSource.deleteAll() }
    override fun getAll(): Flow<List<Audio>> = audioDatabaseSource.getAll()
}