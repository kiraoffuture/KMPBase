package com.kira.kmpbase.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CacheEntryDao {
    @Query("SELECT cache_value FROM cache_entry WHERE cache_key = :key LIMIT 1")
    suspend fun getValue(key: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(entry: CacheEntryEntity)

    @Query("DELETE FROM cache_entry WHERE cache_key = :key")
    suspend fun deleteByKey(key: String)
}
