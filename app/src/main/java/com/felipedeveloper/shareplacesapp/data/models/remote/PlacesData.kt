package com.felipedeveloper.shareplacesapp.data.models.remote

data class PlacesData(
    var placesId: String = "",
    var createdAt: Long = 0,
    var description: String = "",
    var photos: ArrayList<PhotosData>? = null,
    var userId: String = ""
)