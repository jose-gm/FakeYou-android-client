package com.joseg.fakeyouclient.data.repository

import com.joseg.fakeyouclient.data.model.asVoiceModelCompact
import com.joseg.fakeyouclient.model.VoiceModelCompact
import com.joseg.fakeyouclient.network.FakeYouRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class VoiceModelsRepository @Inject constructor(
    private val fakeYouRemoteDataSource: FakeYouRemoteDataSource) {

    fun getVoiceModels(): Flow<List<VoiceModelCompact>> = flow {
        emit(fakeYouRemoteDataSource.getVoiceModels())
    }
        .map { list -> list.map { it.asVoiceModelCompact() }
        }
        .flowOn(Dispatchers.IO)
}