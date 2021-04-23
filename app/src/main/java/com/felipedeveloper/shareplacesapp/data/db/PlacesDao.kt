package com.felipedeveloper.shareplacesapp.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.felipedeveloper.shareplacesapp.data.models.local.Places

@Dao
interface PlacesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlace(data: Places)

}