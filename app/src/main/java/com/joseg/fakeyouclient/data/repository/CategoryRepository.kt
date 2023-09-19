package com.joseg.fakeyouclient.data.repository

import com.joseg.fakeyouclient.data.ApiResult
import com.joseg.fakeyouclient.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getCategories(refresh: Boolean = false): Flow<ApiResult<List<Category>>>
}