package com.aprianto.dicostory.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = Constanta.preferenceName)

class SettingPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    private val token = stringPreferencesKey(Constanta.UserPreferences.UserToken.name)
    private val uid = stringPreferencesKey(Constanta.UserPreferences.UserUID.name)
    private val name = stringPreferencesKey(Constanta.UserPreferences.UserName.name)
    private val email = stringPreferencesKey(Constanta.UserPreferences.UserEmail.name)
    private val lastLogin = stringPreferencesKey(Constanta.UserPreferences.UserLastLogin.name)

    fun getUserToken(): Flow<String> = dataStore.data.map { it[token] ?: Constanta.preferenceDefaultValue }

    fun getUserUid(): Flow<String> = dataStore.data.map { it[uid] ?: Constanta.preferenceDefaultValue }

    fun getUserName(): Flow<String> = dataStore.data.map { it[name] ?: Constanta.preferenceDefaultValue }

    fun getUserEmail(): Flow<String> = dataStore.data.map { it[email] ?: Constanta.preferenceDefaultValue }

    fun getUserLastLogin(): Flow<String> = dataStore.data.map { it[lastLogin] ?: Constanta.preferenceDefaultDateValue }

    suspend fun saveLoginSession(userToken: String, userUid: String, userName:String, userEmail: String) {
        dataStore.edit { preferences ->
            preferences[token] = userToken
            preferences[uid] = userUid
            preferences[name] = userName
            preferences[email] = userEmail
            preferences[lastLogin] = Helper.getCurrentDateString()
        }
    }

    suspend fun clearLoginSession() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: SettingPreferences? = null
        fun getInstance(dataStore: DataStore<Preferences>): SettingPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = SettingPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}