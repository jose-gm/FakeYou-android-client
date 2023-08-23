package com.joseg.fakeyouclient.data.repository

import com.joseg.fakeyouclient.common.Constants
import com.joseg.fakeyouclient.data.cache.InMemoryCache
import com.joseg.fakeyouclient.data.cache.createCacheFlow
import com.joseg.fakeyouclient.data.model.asVoiceModelCompact
import com.joseg.fakeyouclient.model.VoiceModelCompact
import com.joseg.fakeyouclient.network.FakeYouRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.lang.Exception
import javax.inject.Inject

class VoiceModelsRepository @Inject constructor(
    private val fakeYouRemoteDataSource: FakeYouRemoteDataSource,
    private val inMemoryCache: InMemoryCache
) {
    fun getVoiceModels(refresh: Boolean = false): Flow<List<VoiceModelCompact>> = inMemoryCache.createCacheFlow(
        key = Constants.VOICE_MODELS_CACHE_KEY,
        refreshCache = refresh,
        source = { fakeYouRemoteDataSource.getVoiceModels() }
    )
        .map { list -> list.map { it.asVoiceModelCompact() } }
        .flowOn(Dispatchers.IO)
}