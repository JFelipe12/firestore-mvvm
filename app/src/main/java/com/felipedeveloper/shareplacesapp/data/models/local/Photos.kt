package com.felipedeveloper.shareplacesapp.data.models.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "photos_table",
    foreignKeys = [
        ForeignKey(
            entity = Places::class,
            parentColumns = ["placesId"],
            childColumns = ["placesCreatorId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
class Photos(
    @PrimaryKey(autoGenerate = true)
    var photosId: Long?,
    var placesCreatorId: Long?,
    var imageUrl: String?
)