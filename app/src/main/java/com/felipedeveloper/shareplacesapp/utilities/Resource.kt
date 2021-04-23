package com.felipedeveloper.shareplacesapp.utilities

sealed class Resource<out T> {

    class Loading<T> : Resource<T>()
    data class Success<T>(val data: T) : Resource<T>()
    data class Failure<T>(val message: String) : Resource<T>()

}