package com.kira.kmpbase.core.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String,
)

@Serializable
data class BaseResponse<T>(
    val data: T? = null,
)
