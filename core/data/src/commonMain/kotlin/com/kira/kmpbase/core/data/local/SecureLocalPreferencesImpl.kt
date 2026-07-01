package com.kira.kmpbase.core.data.local

import com.kira.kmpbase.core.common.storage.SecureStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class SecureLocalPreferencesImpl(
    private val secureStorage: SecureStorage,
) : SecureLocalPreferences {

    override suspend fun getString(key: String): String? = withContext(Dispatchers.IO) {
        secureStorage.getString(key)
    }

    override suspend fun putString(key: String, value: String) = withContext(Dispatchers.IO) {
        secureStorage.putString(key, value)
    }

    override suspend fun remove(key: String) = withContext(Dispatchers.IO) {
        secureStorage.remove(key)
    }
}
