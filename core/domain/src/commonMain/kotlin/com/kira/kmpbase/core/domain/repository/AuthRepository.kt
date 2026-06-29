package com.kira.kmpbase.core.domain.repository

import com.kira.kmpbase.core.domain.model.AppResult
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val sessionState: StateFlow<Boolean?>

    suspend fun refreshSession()

    suspend fun login(email: String, password: String): AppResult<String>

    suspend fun logout()
}
