package com.joseg.fakeyouclient.ui.shared

import androidx.annotation.StringRes
import com.joseg.fakeyouclient.R
import com.joseg.fakeyouclient.data.ApiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

sealed interface UiState<out T> {
    class Success<T>(val data: T) : UiState<T>
    class Error(val exception: Throwable, @StringRes val errorMessageRes: Int) : UiState<Nothing>
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

fun <T> UiState<T>.onError(callback: (Throwable, Int) -> Unit): UiState<T> {
    if (this is UiState.Error)
        callback(this.exception, this.errorMessageRes)
    return this
}

fun <T> ApiResult<T>.asUiState(): UiState<T> = when (this) {
    is ApiResult.Loading -> UiState.Loading
    is ApiResult.Error -> {
        when (this) {
            is ApiResult.Error.TooManyRequestsError -> UiState.Error(
                this.throwable,
                R.string.error_message_http_code_429
            )
            is ApiResult.Error.ServerError -> UiState.Error(
                this.throwable,
                R.string.error_message_http_code_500
            )
            is ApiResult.Error.GenericError -> UiState.Error(
                this.throwable,
                R.string.error_message_generic
            )
        }
    }
    is ApiResult.Success -> UiState.Success(data)
}

fun <T> Flow<ApiResult<T>>.asUiState(): Flow<UiState<T>> = this.map {
    when (it) {
        is ApiResult.Success -> UiState.Success(it.data)
        is ApiResult.Error -> {
            when (it) {
                is ApiResult.Error.TooManyRequestsError -> UiState.Error(
                    it.throwable,
                    R.string.error_message_http_code_429
                )
                is ApiResult.Error.ServerError -> UiState.Error(
                    it.throwable,
                    R.string.error_message_http_code_500
                )
                is ApiResult.Error.GenericError -> UiState.Error(
                    it.throwable,
                    R.string.error_message_generic
                )
            }
        }
        is ApiResult.Loading -> UiState.Loading
    }
}