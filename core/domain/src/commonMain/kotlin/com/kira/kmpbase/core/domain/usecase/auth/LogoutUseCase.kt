package com.kira.kmpbase.core.domain.usecase.auth

import com.kira.kmpbase.core.domain.repository.AuthRepository

class LogoutUseCase(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke() {
        authRepository.logout()
    }
}
