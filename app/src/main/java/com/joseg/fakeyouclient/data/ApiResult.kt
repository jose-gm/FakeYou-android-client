package com.joseg.fakeyouclient.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import com.joseg.fakeyouclient.common.UiState

sealed interface ApiResult<out T> {
    class Success<T>(val data: T): ApiResult<T>
    class Error(val e: Throwable? = null): ApiResult<Nothing>
    object Loading : ApiResult<Nothing>
}

fun <T : Any> Flow<T>.asApiResult(): Flow<ApiResult<T>> =
    this
        .map<T, ApiResult<T>> { ApiResult.Success(it) }
        .catch { emit(ApiResult.Error(it)) }

fun <T, R> ApiResult<T>.map(transform: (T) -> R): ApiResult<R> = when (this) {
    is ApiResult.Success -> ApiResult.Success(transform(this.data))
    is ApiResult.Error -> this
    is ApiResult.Loading -> this

}

fun <T1, T2, R> combineApiResult(
    apiResult1: ApiResult<T1>,
    apiResult2: ApiResult<T2>,
    transform: (T1, T2) -> R
): ApiResult<R> {
    return if (apiResult1 is ApiResult.Error) {
        ApiResult.Error(apiResult1.e)
    } else if (apiResult2 is ApiResult.Error) {
        ApiResult.Error(apiResult2.e)
    } else if (apiResult1 is ApiResult.Loading) {
        apiResult1
    } else if (apiResult2 is ApiResult.Loading) {
        apiResult2
    } else {
        val data1 = (apiResult1 as ApiResult.Success).data
        val data2 = (apiResult2 as ApiResult.Success).data
        ApiResult.Success(transform(data1, data2))
    }
}