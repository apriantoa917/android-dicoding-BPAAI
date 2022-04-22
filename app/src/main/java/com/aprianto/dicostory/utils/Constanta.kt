package com.aprianto.dicostory.utils

object Constanta {

    enum class UserPreferences {
        UserUID, UserName, UserEmail, UserToken, UserLastLogin
    }

    enum class StoryDetail {
        UserName, ImageURL, ContentDescription, UploadTime, Latitude, Longitude
    }

    enum class LocationPicker {
        isPicked, Latitude, Longitude
    }

    const val preferenceName = "Settings"
    const val preferenceDefaultValue = "Not Set"
    const val preferenceDefaultDateValue = "2000/04/30 00:00:00"
    const val URLPortfolio = "https://apriantoa917.github.io/"
    const val tempToken =
        "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLWZWNkFCelJ6QzFlOE9RckkiLCJpYXQiOjE2NDk4NjMzOTh9.ADTZkrIsu3Kt2P5V_o3sLb0KMEg0CydxFB8CQm1Ao5Q"

    val emailPattern = Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")


}