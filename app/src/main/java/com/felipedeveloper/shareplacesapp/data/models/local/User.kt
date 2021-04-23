package com.felipedeveloper.shareplacesapp.data.models.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class User(
    @PrimaryKey(autoGenerate = true) var userId: Long?,
    var name: String,
    var lastName: String,
    var email: String,
    var password: String
)