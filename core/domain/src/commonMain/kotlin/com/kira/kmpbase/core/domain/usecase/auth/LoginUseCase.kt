package com.kira.kmpbase.core.domain.usecase.auth

import com.kira.kmpbase.core.domain.model.AppError
import com.kira.kmpbase.core.domain.model.AppResult
import com.kira.kmpbase.core.domain.repository.AuthRepository
import com.kira.kmpbase.core.domain.validation.LoginCredentialsValidator

class LoginUseCase(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(email: String, password: String): AppResult<String> {
        LoginCredentialsValidator.validate(email, password)?.let { validationError ->
            return AppResult.Error(AppError.Validation(validationError))
        }
        return authRepository.login(email.trim(), password)
    }
}
