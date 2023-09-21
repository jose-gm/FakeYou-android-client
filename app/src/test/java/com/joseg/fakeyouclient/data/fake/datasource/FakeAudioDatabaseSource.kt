package com.joseg.fakeyouclient.data.fake.datasource

import com.joseg.fakeyouclient.data.localDataSource.AudioLocalDataSource
import com.joseg.fakeyouclient.model.Audio
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FakeAudioDatabaseSource : AudioLocalDataSource {
    private val stateFlow = MutableStateFlow(emptyList<Audio>())

    override suspend fun insert(audio: Audio) {
        stateFlow.update {
            it + audio
        }
    }

    override suspend fun delete(audio: Audio) {
        stateFlow.update {
            it.filterNot { audio -> audio.id == audio.id }
        }
    }

    override suspend fun deleteAll() {
        stateFlow.update {
            it.filterNot { true }
        }
    }

    override fun getAll(): Flow<List<Audio>> = stateFlow
}