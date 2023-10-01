package com.joseg.fakeyouclient.data.localDataSource

import com.joseg.fakeyouclient.database.dao.AudioDao
import com.joseg.fakeyouclient.database.entity.asModel
import com.joseg.fakeyouclient.model.Audio
import com.joseg.fakeyouclient.model.asEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AudioDatabaseSource @Inject constructor(
    private val audioDao: AudioDao
) : AudioLocalDataSource {
    override suspend fun insert(audio: Audio) = audioDao.insert(audio.asEntity())

    override suspend fun delete(audios: List<Audio>) = audioDao.delete(audios.map { it.asEntity() })

    override suspend fun deleteAll() = audioDao.deleteAll()

    override fun getAll(): Flow<List<Audio>> = audioDao.getAll().map {
        it.map { audioEntity -> audioEntity.asModel() }
    }
}