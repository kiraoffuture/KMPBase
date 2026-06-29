package com.kira.kmpbase.core.domain.usecase.auth

import com.kira.kmpbase.core.domain.model.AppError
import com.kira.kmpbase.core.domain.model.AppResult
import com.kira.kmpbase.core.domain.model.ValidationErrorCode
import com.kira.kmpbase.core.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LoginUseCaseTest {

    @Test
    fun invoke_returnsValidationErrorForBlankEmail() = runTest {
        val useCase = LoginUseCase(FakeAuthRepository())

        val result = useCase(email = " ", password = "123456")

        assertTrue(result is AppResult.Error)
        val error = (result as AppResult.Error).error
        assertTrue(error is AppError.Validation)
        assertEquals(ValidationErrorCode.EMAIL_REQUIRED, error.code)
    }

    @Test
    fun invoke_delegatesToRepositoryWhenCredentialsAreValid() = runTest {
        val repository = FakeAuthRepository(loginResult = AppResult.Success("token"))
        val useCase = LoginUseCase(repository)

        val result = useCase(email = "user@example.com", password = "123456")

        assertEquals(AppResult.Success("token"), result)
        assertEquals("user@example.com", repository.lastLoginEmail)
    }

    private class FakeAuthRepository(
        private val loginResult: AppResult<String> = AppResult.Success("token"),
    ) : AuthRepository {
        var lastLoginEmail: String? = null
        private val _sessionState = MutableStateFlow<Boolean?>(false)

        override val sessionState: StateFlow<Boolean?> = _sessionState

        override suspend fun refreshSession() = Unit

        override suspend fun login(email: String, password: String): AppResult<String> {
            lastLoginEmail = email
            return loginResult
        }

        override suspend fun logout() = Unit
    }
}
