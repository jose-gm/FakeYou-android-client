package com.joseg.fakeyouclient.data.repository.implementation

import com.joseg.fakeyouclient.common.Constants
import com.joseg.fakeyouclient.data.ApiResult
import com.joseg.fakeyouclient.data.asApiResult
import com.joseg.fakeyouclient.data.cache.MemoryCache
import com.joseg.fakeyouclient.data.cache.createCacheFlow
import com.joseg.fakeyouclient.data.model.asCategories
import com.joseg.fakeyouclient.data.repository.CategoryRepository
import com.joseg.fakeyouclient.model.Category
import com.joseg.fakeyouclient.network.FakeYouRemoteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BaseCategoryRepository @Inject constructor(
    private val fakeYouRemoteDataSource: FakeYouRemoteDataSource,
    private val memoryCache: MemoryCache,
    private val i0Dispatcher: CoroutineDispatcher
) : CategoryRepository {
    override fun getCategories(refresh: Boolean): Flow<ApiResult<List<Category>>> = memoryCache.createCacheFlow(
        key = Constants.CATEGORIES_MODELS_CACHE_KEY,
        refreshCache = refresh,
        source = { fakeYouRemoteDataSource.getCategories() }
    )
        .map {
            it.asCategories()
        }
        .asApiResult()
        .flowOn(i0Dispatcher)
}