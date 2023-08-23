package com.joseg.fakeyouclient.data.repository

import com.joseg.fakeyouclient.common.Constants
import com.joseg.fakeyouclient.data.cache.InMemoryCache
import com.joseg.fakeyouclient.data.cache.createCacheFlow
import com.joseg.fakeyouclient.data.model.asCategoriesCompact
import com.joseg.fakeyouclient.model.CategoryCompact
import com.joseg.fakeyouclient.network.FakeYouRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.lang.Exception
import javax.inject.Inject

class CategoriesRepository @Inject constructor(
    private val fakeYouRemoteDataSource: FakeYouRemoteDataSource,
    private val inMemoryCache: InMemoryCache
) {
    fun getCategories(refresh: Boolean = false): Flow<List<CategoryCompact>> = inMemoryCache.createCacheFlow(
        key = Constants.CATEGORIES_MODELS_CACHE_KEY,
        refreshCache = refresh,
        source = { fakeYouRemoteDataSource.getCategories() }
    )
        .map { it.asCategoriesCompact() }
        .flowOn(Dispatchers.IO)
}