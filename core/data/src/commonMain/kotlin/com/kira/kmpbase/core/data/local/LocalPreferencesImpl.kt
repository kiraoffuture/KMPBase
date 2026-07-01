package com.kira.kmpbase.core.data.local

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class LocalPreferencesImpl(
    private val settings: Settings,
) : LocalPreferences {

    override suspend fun getString(key: String, defaultValue: String): String = withContext(Dispatchers.IO) {
        settings.getString(key, defaultValue)
    }

    override suspend fun putString(key: String, value: String) = withContext(Dispatchers.IO) {
        settings[key] = value
    }
}
