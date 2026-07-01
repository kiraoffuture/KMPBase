package com.kira.kmpbase.core.common.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AndroidSecureStorage(
    context: Context,
) : SecureStorage {

    private val preferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREFS_FILE_NAME,
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    override suspend fun getString(key: String): String? = withContext(Dispatchers.IO) {
        preferences.getString(key, null)
    }

    override suspend fun putString(key: String, value: String) = withContext(Dispatchers.IO) {
        preferences.edit().putString(key, value).apply()
    }

    override suspend fun remove(key: String) = withContext(Dispatchers.IO) {
        preferences.edit().remove(key).apply()
    }

    private companion object {
        const val PREFS_FILE_NAME = "kmp_secure_storage"
    }
}
