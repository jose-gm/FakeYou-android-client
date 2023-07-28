package com.joseg.fakeyouclient.common

sealed interface Result<out T> {
    class Success<T>(val data: T) : Result<T>
    class Error(val message: String) : Result<Nothing>
    object Loading : Result<Nothing>
}
