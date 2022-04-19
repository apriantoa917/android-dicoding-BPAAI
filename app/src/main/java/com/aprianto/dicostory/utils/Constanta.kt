package com.aprianto.dicostory.utils

object Constanta {

    enum class UserPreferences {
        UserUID, UserName, UserEmail, UserToken, UserLastLogin
    }

    enum class StoryDetail {
        UserName, ImageURL, ContentDescription, UploadTime
    }

    const val preferenceName = "Settings"
    const val preferenceDefaultValue = "Not Set"
    const val preferenceDefaultDateValue = "2000/04/30 00:00:00"
    const val URLPortfolio = "https://apriantoa917.github.io/"

    val emailPattern = Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")


}