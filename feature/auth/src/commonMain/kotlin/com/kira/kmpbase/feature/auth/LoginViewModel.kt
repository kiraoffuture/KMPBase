package com.kira.kmpbase.feature.auth

import androidx.lifecycle.ViewModel
import com.kira.kmpbase.core.domain.model.AppResult
import com.kira.kmpbase.core.domain.usecase.auth.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.kira.kmpbase.core.ui.viewmodel.launchSafely

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        launchSafely {
            _uiState.update { LoginUiState(isLoading = true) }
            _uiState.update {
                when (val result = loginUseCase(email, password)) {
                    is AppResult.Success -> LoginUiState(token = result.data)
                    is AppResult.Error -> result.toLoginUiState()
                    AppResult.Loading -> LoginUiState()
                }
            }
        }
    }
}
