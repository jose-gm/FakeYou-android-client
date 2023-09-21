package com.joseg.fakeyouclient.data.fake.repository

import com.joseg.fakeyouclient.data.ApiResult
import com.joseg.fakeyouclient.data.repository.CategoryRepository
import com.joseg.fakeyouclient.data.testdouble.model.CategoryDummies
import com.joseg.fakeyouclient.model.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeCategoryRepository : CategoryRepository {
    override fun getCategories(refresh: Boolean): Flow<ApiResult<List<Category>>> = flow {
        emit(ApiResult.toApiResult { CategoryDummies.dummyList })
    }
}