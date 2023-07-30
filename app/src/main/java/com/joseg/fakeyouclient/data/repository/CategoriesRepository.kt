package com.joseg.fakeyouclient.data.repository

import com.joseg.fakeyouclient.common.Result
import com.joseg.fakeyouclient.common.asResult
import com.joseg.fakeyouclient.common.mapResult
import com.joseg.fakeyouclient.data.model.asCategoriesParentCompact
import com.joseg.fakeyouclient.model.ParentCategoryCompat
import com.joseg.fakeyouclient.network.service.FakeYouApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class CategoriesRepository @Inject constructor(private val fakeYouApi: FakeYouApi) {

    fun getCategories(): Flow<Result<List<ParentCategoryCompat>>> = flow {
        emit(fakeYouApi.getCategoriesVoices())
    }
        .asResult()
        .mapResult { it.asCategoriesParentCompact() }
        .flowOn(Dispatchers.IO)
}