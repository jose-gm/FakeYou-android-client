package com.joseg.fakeyouclient.common

import com.joseg.fakeyouclient.data.ApiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

sealed interface UiState<out T> {
    class Success<T>(val data: T) : UiState<T>
    class Error(val exception: Throwable? = null) : UiState<Nothing>
    object Loading : UiState<Nothing>
}

fun <T> UiState<T>.onSuccess(callback: (T) -> Unit): UiState<T> {
    if (this is UiState.Success)
        callback(this.data)
    return this
}

fun <T> UiState<T>.onLoading(callback: () -> Unit): UiState<T> {
    if (this is UiState.Loading)
        callback()
    return this
}

fun <T> UiState<T>.onError(callback: (Throwable?) -> Unit): UiState<T> {
    if (this is UiState.Error)
        callback(this.exception)
    return this
}

fun <T> Flow<ApiResult<T>>.asUiState(): Flow<UiState<T>> = this.map {
    when (it) {
        is ApiResult.Success -> UiState.Success(it.data)
        is ApiResult.Error -> UiState.Error(it.e)
        is ApiResult.Loading -> UiState.Loading
    }
}