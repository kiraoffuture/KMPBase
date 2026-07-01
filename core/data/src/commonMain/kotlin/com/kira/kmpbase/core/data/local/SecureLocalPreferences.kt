package com.kira.kmpbase.core.data.local

interface SecureLocalPreferences {
    suspend fun getString(key: String): String?
    suspend fun putString(key: String, value: String)
    suspend fun remove(key: String)
}
