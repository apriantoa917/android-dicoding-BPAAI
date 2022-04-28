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

    val indonesiaLocation = LatLng(-2.3932797, 108.8507139)
    const val preferenceName = "Settings"
    const val preferenceDefaultValue = "Not Set"
    const val preferenceDefaultDateValue = "2000/04/30 00:00:00"
    const val URLPortfolio = "https://apriantoa917.github.io/"

    val emailPattern = Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")


    /* PERMISSION REQUEST CODE */
    const val CAMERA_PERMISSION_CODE = 10
    const val STORAGE_PERMISSION_CODE = 20
    const val LOCATION_PERMISSION_CODE = 30
    const val tempToken =
        "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLWZWNkFCelJ6QzFlOE9RckkiLCJpYXQiOjE2NTEwMjE4MzB9.fNi8G9VXnv8Sg1EHJq2KHOeMg_tbhLuo2Hqd6YMacK4"

    const val TAG_WIDGET = "WIDGET_STORY"
    const val TAG_STORY = "TEST_STORY"
    const val TAG_MAPS = "TEST_MAPS"
    const val TAG_DOWNLOAD = "TEST_DOWNLOAD"
    const val TAG_AUTH = "TEST_AUTH"
}