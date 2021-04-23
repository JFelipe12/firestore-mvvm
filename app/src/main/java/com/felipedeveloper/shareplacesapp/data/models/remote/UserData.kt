package com.felipedeveloper.shareplacesapp.data.models.remote

data class UserData(
    var name: String = "",
    var email: String = "",
    var password: String = "",
    var location: LocationData? = null
)