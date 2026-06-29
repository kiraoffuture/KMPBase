package com.kira.kmpbase.core.network

import com.kira.kmpbase.core.model.LoginRequest

class AuthApiService(
    private val authApi: AuthApi,
) {
    suspend fun login(email: String, password: String): String {
        val token = authApi.login(
            LoginRequest(
                email = email.trim(),
                password = password,
            ),
        ).data
        require(!token.isNullOrBlank()) { "Login response did not contain a token" }
        return token
    }
}
