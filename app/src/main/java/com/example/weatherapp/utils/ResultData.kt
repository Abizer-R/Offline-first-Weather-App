package com.example.weatherapp.utils

sealed class ResultData<out T> {

    data class Success<T>(val data: T) : ResultData<T>()

    data class Failed(
        val exception: Throwable? = null,
        val message: String? = null,
        val errorBody: String? = null
    ) : ResultData<Nothing>()

    data class Loading(val nothing: Nothing? = null) : ResultData<Nothing>()
}