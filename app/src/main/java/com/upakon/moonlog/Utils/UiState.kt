package com.upakon.moonlog.Utils

sealed class UiState<out T> {

    object LOADING : UiState<Nothing>()

    data class SUCCESS<T>(val data: T) : UiState<T>()

    data class ERROR(val error: Exception) : UiState<Nothing>()

}