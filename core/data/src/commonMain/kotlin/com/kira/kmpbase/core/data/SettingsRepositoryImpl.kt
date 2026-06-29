package com.kira.kmpbase.core.data

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class SettingsRepositoryImpl(
    private val settings: Settings,
) : SettingsRepository {

    override suspend fun getString(key: String, defaultValue: String): String = withContext(Dispatchers.IO) {
        settings.getString(key, defaultValue)
    }

    override suspend fun putString(key: String, value: String) = withContext(Dispatchers.IO) {
        settings[key] = value
    }

    override suspend fun getAuthToken(): String = getString(KEY_AUTH_TOKEN)

    override suspend fun setAuthToken(token: String) = putString(KEY_AUTH_TOKEN, token)

    companion object {
        const val KEY_AUTH_TOKEN = "auth_token"
    }
}
