package com.kira.kmpbase.feature.auth

import com.kira.kmpbase.core.domain.model.AppError
import com.kira.kmpbase.core.domain.model.AppResult

data class LoginUiState(
    val isLoading: Boolean = false,
    val token: String? = null,
    val error: AppError? = null,
)

fun AppResult.Error.toLoginUiState(): LoginUiState = LoginUiState(error = error)
