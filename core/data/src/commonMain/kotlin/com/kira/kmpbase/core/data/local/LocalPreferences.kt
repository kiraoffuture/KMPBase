package com.kira.kmpbase.core.data.local

interface LocalPreferences {
    suspend fun getString(key: String, defaultValue: String = ""): String
    suspend fun putString(key: String, value: String)
}
