package com.joseg.fakeyouclient.data.repository

import com.joseg.fakeyouclient.common.Constants
import com.joseg.fakeyouclient.data.cache.MemoryCache
import com.joseg.fakeyouclient.data.cache.createCacheFlow
import com.joseg.fakeyouclient.data.model.asCategories
import com.joseg.fakeyouclient.model.Category
import com.joseg.fakeyouclient.network.FakeYouRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoriesRepository @Inject constructor(
    private val fakeYouRemoteDataSource: FakeYouRemoteDataSource,
    private val memoryCache: MemoryCache
) {
    fun getCategories(refresh: Boolean = false): Flow<List<Category>> = memoryCache.createCacheFlow(
        key = Constants.CATEGORIES_MODELS_CACHE_KEY,
        refreshCache = refresh,
        source = { fakeYouRemoteDataSource.getCategories() }
    )
        .map { it.asCategories() }
        .flowOn(Dispatchers.IO)
}