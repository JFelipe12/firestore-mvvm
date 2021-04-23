package com.felipedeveloper.shareplacesapp.data.repository.remote

import java.lang.Exception

interface RealtimeDataListener<T> {

    fun onDataChange(updatedData: T)

    fun onError(exception: Exception)

}
