package com.felipedeveloper.shareplacesapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.felipedeveloper.shareplacesapp.data.models.local.Photos
import com.felipedeveloper.shareplacesapp.data.models.local.Places
import com.felipedeveloper.shareplacesapp.data.models.local.User

@Database(
    entities = [Places::class, Photos::class, User::class],
    version = 1,
    exportSchema = false
)
abstract class PlacesDatabase : RoomDatabase() {

    abstract fun getDao(): PlacesDao

    companion object {
        const val DB_NAME = "places.db"
    }
}