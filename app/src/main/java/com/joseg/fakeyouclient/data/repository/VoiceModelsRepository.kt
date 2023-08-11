package com.joseg.fakeyouclient.data.repository

import com.joseg.fakeyouclient.data.model.asVoiceModelCompact
import com.joseg.fakeyouclient.data.model.asVoiceModelsCompact
import com.joseg.fakeyouclient.model.VoiceModelCompact
import com.joseg.fakeyouclient.network.FakeYouRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VoiceModelsRepository @Inject constructor(
    private val fakeYouRemoteDataSource: FakeYouRemoteDataSource
) {
    private val cacheFlow = MutableStateFlow<List<VoiceModelCompact>>(emptyList())

    fun getVoiceModels(refresh: Boolean = false): Flow<List<VoiceModelCompact>> {
        if (!refresh || cacheFlow.value.isNotEmpty())
            return cacheFlow
        return flow {
            val voiceModelsCompact = fakeYouRemoteDataSource.getVoiceModels().asVoiceModelsCompact()
            cacheFlow.emit(voiceModelsCompact)
            emit(voiceModelsCompact)
        }
            .flowOn(Dispatchers.IO)
    }
}