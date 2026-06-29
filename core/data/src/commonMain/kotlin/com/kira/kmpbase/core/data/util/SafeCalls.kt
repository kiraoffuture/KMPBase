package com.kira.kmpbase.core.data.util

import com.kira.kmpbase.core.domain.model.AppError
import com.kira.kmpbase.core.domain.model.AppResult

suspend fun <T> safeApiCall(block: suspend () -> T): AppResult<T> {
    return try {
        AppResult.Success(block())
    } catch (e: Exception) {
        AppResult.Error(AppError.Network(e.message ?: "Network error", e))
    }
}

suspend fun <T> safeDatabaseCall(block: suspend () -> T): AppResult<T> {
    return try {
        AppResult.Success(block())
    } catch (e: Exception) {
        AppResult.Error(AppError.Database(e.message ?: "Database error", e))
    }
}

suspend fun <T> safeStorageCall(block: suspend () -> T): AppResult<T> {
    return try {
        AppResult.Success(block())
    } catch (e: Exception) {
        AppResult.Error(AppError.Unknown(e.message ?: "Storage error", e))
    }
}
