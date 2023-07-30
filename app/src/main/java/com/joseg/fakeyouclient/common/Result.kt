package com.joseg.fakeyouclient.common

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

sealed interface Result<out T> {
    class Success<T>(val data: T) : Result<T>
    class Error(val exception: Throwable? = null) : Result<Nothing>
    object Loading : Result<Nothing>
}

fun <T : Any> Flow<T>.asResult(): Flow<Result<T>> =
    this
        .map<T, Result<T>> { Result.Success(it) }
        .onStart { emit(Result.Loading) }
        .catch { emit(Result.Error(it)) }

fun <T : Any, R: Any> Flow<Result<T>>.mapResult(transform: (value: T) -> R): Flow<Result<R>> =
    this.map {
        when (it) {
            is Result.Success -> Result.Success(transform(it.data))
            is Result.Loading -> Result.Loading
            is Result.Error -> Result.Error(it.exception)
        }
    }