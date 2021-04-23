package com.felipedeveloper.shareplacesapp.data.convertes

import com.felipedeveloper.shareplacesapp.data.models.local.Places
import com.felipedeveloper.shareplacesapp.data.models.remote.PlacesData
import java.util.*

object PlacesConverter {

    fun fromData(binding: PlacesData) =  Places(
        if (binding.placesId.isBlank()) UUID.randomUUID().toString() else binding.userId,
        userCreatorId = binding.userId,
        description = binding.description,
        createdAt = binding.createdAt
    )

}