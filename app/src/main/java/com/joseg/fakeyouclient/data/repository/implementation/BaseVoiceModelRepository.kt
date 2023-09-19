package com.joseg.fakeyouclient.data.repository.implementation

import com.joseg.fakeyouclient.common.Constants
import com.joseg.fakeyouclient.data.ApiResult
import com.joseg.fakeyouclient.data.asApiResult
import com.joseg.fakeyouclient.data.cache.MemoryCache
import com.joseg.fakeyouclient.data.cache.createCacheFlow
import com.joseg.fakeyouclient.data.model.asVoiceModels
import com.joseg.fakeyouclient.data.repository.VoiceModelRepository
import com.joseg.fakeyouclient.datastore.VoiceModelPreferencesDataSource
import com.joseg.fakeyouclient.model.VoiceModel
import com.joseg.fakeyouclient.network.FakeYouRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class BaseVoiceModelRepository @Inject constructor(
    private val fakeYouRemoteDataSource: FakeYouRemoteDataSource,
    private val voiceModelPreferencesDataSource: VoiceModelPreferencesDataSource,
    private val memoryCache: MemoryCache
) : VoiceModelRepository {
    override fun getVoiceModels(refresh: Boolean): Flow<ApiResult<List<VoiceModel>>> = memoryCache.createCacheFlow(
        key = Constants.VOICE_MODELS_CACHE_KEY,
        refreshCache = refresh,
        source = { fakeYouRemoteDataSource.getVoiceModels() }
    )
        .map { it.asVoiceModels() }
        .asApiResult()
        .onStart { emit(ApiResult.Loading) }
        .flowOn(Dispatchers.IO)

    override fun saveVoiceModel(voiceModel: VoiceModel) {
        voiceModelPreferencesDataSource.saveVoiceModel(voiceModel)
    }

    override fun getVoiceModelSync(): VoiceModel? = voiceModelPreferencesDataSource.getVoiceModelSync()
}