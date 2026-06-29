package com.kira.kmpbase.core.database

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class CacheDao(
    private val database: AppDatabase,
) {
    private val cacheEntryDao = database.cacheEntryDao()

    suspend fun get(key: String): String? = withContext(Dispatchers.IO) {
        cacheEntryDao.getValue(key)
    }

    suspend fun put(key: String, value: String, updatedAt: Long) = withContext(Dispatchers.IO) {
        cacheEntryDao.insertOrReplace(
            CacheEntryEntity(
                cacheKey = key,
                cacheValue = value,
                updatedAt = updatedAt,
            ),
        )
    }

    suspend fun delete(key: String) = withContext(Dispatchers.IO) {
        cacheEntryDao.deleteByKey(key)
    }
}
