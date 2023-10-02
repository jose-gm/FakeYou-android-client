package com.joseg.fakeyouclient.data.fake.repository

import com.joseg.fakeyouclient.data.repository.AudioRepository
import com.joseg.fakeyouclient.model.Audio
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FakeAudioRepository : AudioRepository {
    private val flow = MutableStateFlow(emptyList<Audio>())

    override suspend fun insert(audio: Audio) {
        flow.update {
            it.toMutableList().apply {
                add(audio)
            }
        }
    }

    override suspend fun delete(audios: List<Audio>) {
        flow.update {
            it.toMutableList().apply {
                removeAll(audios)
            }
        }
    }

    override suspend fun deleteAll() {
        flow.update {
            it.toMutableList().apply {
                clear()
            }
        }
    }

    override fun getAll(): Flow<List<Audio>> = flow
}