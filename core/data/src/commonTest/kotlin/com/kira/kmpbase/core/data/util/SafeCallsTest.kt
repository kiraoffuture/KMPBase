package com.kira.kmpbase.core.data.util

import com.kira.kmpbase.core.domain.model.AppError
import com.kira.kmpbase.core.domain.model.AppResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SafeCallsTest {

    @Test
    fun safeApiCall_returnsSuccessOnHappyPath() = runTest {
        val result = safeApiCall { "ok" }

        assertTrue(result is AppResult.Success)
        assertEquals("ok", (result as AppResult.Success).data)
    }

    @Test
    fun safeApiCall_returnsNetworkErrorOnException() = runTest {
        val result = safeApiCall<String> { error("boom") }

        assertTrue(result is AppResult.Error)
        val error = (result as AppResult.Error).error
        assertTrue(error is AppError.Network)
        assertEquals("boom", (error as AppError.Network).message)
    }

    @Test
    fun safeDatabaseCall_returnsDatabaseErrorOnException() = runTest {
        val result = safeDatabaseCall<String> { error("db down") }

        assertTrue(result is AppResult.Error)
        val error = (result as AppResult.Error).error
        assertTrue(error is AppError.Database)
        assertEquals("db down", (error as AppError.Database).message)
    }

    @Test
    fun safeStorageCall_returnsUnknownErrorOnException() = runTest {
        val result = safeStorageCall<String> { error("storage down") }

        assertTrue(result is AppResult.Error)
        val error = (result as AppResult.Error).error
        assertTrue(error is AppError.Unknown)
        assertEquals("storage down", (error as AppError.Unknown).message)
    }
}
