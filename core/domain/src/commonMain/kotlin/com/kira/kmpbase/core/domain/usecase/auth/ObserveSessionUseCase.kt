package com.kira.kmpbase.core.domain.usecase.auth

import com.kira.kmpbase.core.domain.repository.AuthRepository
import kotlinx.coroutines.flow.StateFlow

class ObserveSessionUseCase(
    private val authRepository: AuthRepository,
) {
    operator fun invoke(): StateFlow<Boolean?> = authRepository.sessionState
}
