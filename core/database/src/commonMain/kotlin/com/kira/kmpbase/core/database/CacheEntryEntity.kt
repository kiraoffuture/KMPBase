package com.kira.kmpbase.core.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cache_entry")
data class CacheEntryEntity(
    @PrimaryKey
    @ColumnInfo(name = "cache_key")
    val cacheKey: String,
    @ColumnInfo(name = "cache_value")
    val cacheValue: String,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,
)
