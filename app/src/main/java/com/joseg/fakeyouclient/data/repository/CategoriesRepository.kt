package com.joseg.fakeyouclient.data.repository

import com.joseg.fakeyouclient.data.model.asParentCategoriesCompact
import com.joseg.fakeyouclient.model.ParentCategoryCompat
import com.joseg.fakeyouclient.model.VoiceModelCompact
import com.joseg.fakeyouclient.network.FakeYouRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoriesRepository @Inject constructor(
    private val fakeRemoteDataSource: FakeYouRemoteDataSource
) {
    private val cacheFlow = MutableStateFlow<List<ParentCategoryCompat>>(emptyList())

    fun getCategories(refresh: Boolean = false): Flow<List<ParentCategoryCompat>> {
        if (!refresh || cacheFlow.value.isNotEmpty())
            return cacheFlow
        return flow {
            val parentCategoriesCompact = fakeRemoteDataSource.getCategories().asParentCategoriesCompact()
            emit(parentCategoriesCompact)
            cacheFlow.emit(parentCategoriesCompact)
        }
            .flowOn(Dispatchers.IO)
    }
}