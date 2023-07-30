package com.joseg.fakeyouclient.data.repository

import com.joseg.fakeyouclient.common.Result
import com.joseg.fakeyouclient.common.asResult
import com.joseg.fakeyouclient.common.mapResult
import com.joseg.fakeyouclient.data.model.asVoiceModelsCompact
import com.joseg.fakeyouclient.model.VoiceModelCompact
import com.joseg.fakeyouclient.network.service.FakeYouApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class VoiceModelsRepository @Inject constructor(private val fakeYouApi: FakeYouApi) {

    fun getVoiceModels(): Flow<Result<List<VoiceModelCompact>>> = flow {
        emit(fakeYouApi.getVoiceModels())
    }
        .asResult()
        .mapResult { it.asVoiceModelsCompact() }
        .flowOn(Dispatchers.IO)
}