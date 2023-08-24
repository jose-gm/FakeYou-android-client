package com.joseg.fakeyouclient.data.repository

import com.joseg.fakeyouclient.common.Constants
import com.joseg.fakeyouclient.data.cache.MemoryCache
import com.joseg.fakeyouclient.data.cache.createCacheFlow
import com.joseg.fakeyouclient.data.model.asVoiceModels
import com.joseg.fakeyouclient.model.VoiceModel
import com.joseg.fakeyouclient.network.FakeYouRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class VoiceModelsRepository @Inject constructor(
    private val fakeYouRemoteDataSource: FakeYouRemoteDataSource,
    private val memoryCache: MemoryCache
) {
    fun getVoiceModels(refresh: Boolean = false): Flow<List<VoiceModel>> = memoryCache.createCacheFlow(
        key = Constants.VOICE_MODELS_CACHE_KEY,
        refreshCache = refresh,
        source = { fakeYouRemoteDataSource.getVoiceModels() }
    )
        .map { it.asVoiceModels() }
        .flowOn(Dispatchers.IO)
}