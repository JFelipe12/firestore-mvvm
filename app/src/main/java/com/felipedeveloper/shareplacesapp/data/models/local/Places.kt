package com.felipedeveloper.shareplacesapp.data.models.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

@Entity(tableName = "places_table")
data class Places(
    @PrimaryKey var placesId: String,
    var userCreatorId: String = "",
    var description: String = "",
    var createdAt: Long = 0,
)