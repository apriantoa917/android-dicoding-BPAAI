package com.aprianto.dicostory.utils

import com.google.android.gms.maps.model.LatLng

object Constanta {

    enum class UserPreferences {
        UserUID, UserName, UserEmail, UserToken, UserLastLogin
    }

    enum class StoryDetail {
        UserName, ImageURL, ContentDescription, UploadTime, Latitude, Longitude
    }

    enum class LocationPicker {
        IsPicked, Latitude, Longitude
    }

    enum class RequestPermission {
        All, Camera, Storage, Location
    }


    val indonesiaLocation = LatLng(-2.3932797, 108.8507139)
    const val preferenceName = "Settings"
    const val preferenceDefaultValue = "Not Set"
    const val preferenceDefaultDateValue = "2000/04/30 00:00:00"
    const val URLPortfolio = "https://apriantoa917.github.io/"
//    const val tempToken =
//        "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLWZWNkFCelJ6QzFlOE9RckkiLCJpYXQiOjE2NDk4NjMzOTh9.ADTZkrIsu3Kt2P5V_o3sLb0KMEg0CydxFB8CQm1Ao5Q"

    val emailPattern = Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")


    /* PERMISSION REQUEST CODE */
    const val CAMERA_PERMISSION_CODE = 10
    const val STORAGE_PERMISSION_CODE = 20
    const val LOCATION_PERMISSION_CODE = 30

    const val tempToken =
        "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLWZWNkFCelJ6QzFlOE9RckkiLCJpYXQiOjE2NTEwMjE4MzB9.fNi8G9VXnv8Sg1EHJq2KHOeMg_tbhLuo2Hqd6YMacK4"
}