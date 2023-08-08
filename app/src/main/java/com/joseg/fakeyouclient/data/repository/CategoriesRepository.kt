package com.joseg.fakeyouclient.data.repository

import com.joseg.fakeyouclient.data.model.asParentCategoriesCompact
import com.joseg.fakeyouclient.model.ParentCategoryCompat
import com.joseg.fakeyouclient.network.FakeYouRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoriesRepository @Inject constructor(
    private val fakeRemoteDataSource: FakeYouRemoteDataSource
) {

    fun getCategories(): Flow<List<ParentCategoryCompat>> = flow {
        emit(fakeRemoteDataSource.getCategories())
    }
        .map { list -> list.asParentCategoriesCompact() }
        .flowOn(Dispatchers.IO)
}