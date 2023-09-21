package com.joseg.fakeyouclient.data.fake.repository

import com.joseg.fakeyouclient.data.ApiResult
import com.joseg.fakeyouclient.data.repository.VoiceModelRepository
import com.joseg.fakeyouclient.data.testdouble.model.VoiceModelDummies
import com.joseg.fakeyouclient.model.VoiceModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeVoiceModelRepository : VoiceModelRepository {
    private var cache: VoiceModel? = VoiceModelDummies.dummy2

    override fun getVoiceModels(refresh: Boolean): Flow<ApiResult<List<VoiceModel>>> = flow {
        emit(ApiResult.toApiResult { VoiceModelDummies.dummyList })
    }

    override fun saveVoiceModel(voiceModel: VoiceModel) {
        cache = voiceModel
    }

    override fun getVoiceModelSync(): VoiceModel? = cache
}