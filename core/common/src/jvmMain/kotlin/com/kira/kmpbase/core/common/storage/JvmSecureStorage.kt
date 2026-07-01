package com.kira.kmpbase.core.common.storage

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.prefs.Preferences

/**
 * Desktop fallback — not hardware-backed. Mobile apps use Android/iOS secure storage.
 */
class JvmSecureStorage : SecureStorage {

    private val preferences: Preferences =
        Preferences.userRoot().node("com/kira/kmpbase/secure")

    override suspend fun getString(key: String): String? = withContext(Dispatchers.IO) {
        preferences.get(key, null)
    }

    override suspend fun putString(key: String, value: String) = withContext(Dispatchers.IO) {
        preferences.put(key, value)
        preferences.flush()
    }

    override suspend fun remove(key: String) = withContext(Dispatchers.IO) {
        preferences.remove(key)
        preferences.flush()
    }
}
