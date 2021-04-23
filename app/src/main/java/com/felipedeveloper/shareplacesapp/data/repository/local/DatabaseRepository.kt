package com.felipedeveloper.shareplacesapp.data.repository.local

import com.felipedeveloper.shareplacesapp.data.db.PlacesDao
import com.felipedeveloper.shareplacesapp.data.models.local.Places
import javax.inject.Inject

class DatabaseRepository @Inject constructor(private val dao: PlacesDao) {

    suspend fun insertPlace(data: Places) = dao.insertPlace(data)

}