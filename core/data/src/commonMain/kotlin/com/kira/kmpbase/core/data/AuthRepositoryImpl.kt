package com.kira.kmpbase.core.data

import com.kira.kmpbase.core.data.util.safeApiCall
import com.kira.kmpbase.core.data.util.safeStorageCall
import com.kira.kmpbase.core.domain.model.AppError
import com.kira.kmpbase.core.domain.model.AppResult
import com.kira.kmpbase.core.domain.repository.AuthRepository
import com.kira.kmpbase.core.network.AuthApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepositoryImpl(
    private val authApiService: AuthApiService,
    private val settingsRepository: SettingsRepository,
) : AuthRepository {

    private val _sessionState = MutableStateFlow<Boolean?>(null)
    override val sessionState: StateFlow<Boolean?> = _sessionState.asStateFlow()

    override suspend fun refreshSession() {
        when (val result = safeStorageCall { settingsRepository.getAuthToken() }) {
            is AppResult.Success -> _sessionState.value = result.data.isNotBlank()
            is AppResult.Error -> _sessionState.value = false
            AppResult.Loading -> Unit
        }
    }

    override suspend fun login(email: String, password: String): AppResult<String> {
        return when (val result = safeApiCall { authApiService.login(email, password) }) {
            is AppResult.Success -> when (
                val saveResult = safeStorageCall { settingsRepository.setAuthToken(result.data) }
            ) {
                is AppResult.Success -> {
                    _sessionState.value = true
                    result
                }
                is AppResult.Error -> saveResult
                AppResult.Loading -> AppResult.Error(AppError.Unknown("Unexpected loading state"))
            }
            is AppResult.Error -> result
            AppResult.Loading -> AppResult.Error(AppError.Unknown("Unexpected loading state"))
        }
    }

    override suspend fun logout() {
        when (safeStorageCall { settingsRepository.setAuthToken("") }) {
            is AppResult.Success -> _sessionState.value = false
            is AppResult.Error -> _sessionState.value = false
            AppResult.Loading -> Unit
        }
    }
}
