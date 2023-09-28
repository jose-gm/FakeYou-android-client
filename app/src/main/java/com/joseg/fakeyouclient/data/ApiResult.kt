package com.joseg.fakeyouclient.data

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import retrofit2.HttpException

sealed interface ApiResult<out T> {
    class Success<T>(val data: T): ApiResult<T>
    object Loading : ApiResult<Nothing>
    sealed interface Error : ApiResult<Nothing> {
        class TooManyRequestsError(val throwable: Throwable) : Error
        class ServerError(val throwable: Throwable) : Error
        class GenericError(val throwable: Throwable) : Error
    }

    companion object {
        suspend fun <T: Any> toApiResult(block: suspend () -> T): ApiResult<T> {
            return try {
                Success(block())
            } catch (e: Throwable) {
                Log.e("ttsRequest", e.stackTraceToString())
                mapError(e)
            }
        }
    }
}

private fun mapError(throwable: Throwable): ApiResult.Error = when (throwable) {
    is HttpException -> {
        when {
            throwable.code() == 429 -> ApiResult.Error.TooManyRequestsError(throwable)
            throwable.code() >= 500 -> ApiResult.Error.ServerError(throwable)
            else -> ApiResult.Error.GenericError(throwable)
        }
    }
    else -> ApiResult.Error.GenericError(throwable)
}

fun <T: Any> Flow<T>.asApiResultFlow(): Flow<ApiResult<T>> =
    this
        .map<T, ApiResult<T>> { ApiResult.Success(it) }
        .catch { emit(mapError(it)) }

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
        apiResult1
    } else if (apiResult2 is ApiResult.Error) {
        apiResult2
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