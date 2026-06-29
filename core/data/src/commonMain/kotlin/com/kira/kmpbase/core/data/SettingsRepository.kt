package com.kira.kmpbase.core.data

interface SettingsRepository {
    suspend fun getString(key: String, defaultValue: String = ""): String
    suspend fun putString(key: String, value: String)
    suspend fun getAuthToken(): String
    suspend fun setAuthToken(token: String)
}
