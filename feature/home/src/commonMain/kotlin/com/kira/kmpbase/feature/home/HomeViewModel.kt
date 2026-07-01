package com.kira.kmpbase.feature.home

import androidx.lifecycle.ViewModel
import com.kira.kmpbase.core.domain.model.AppResult
import com.kira.kmpbase.core.domain.usecase.home.ObserveContactsUseCase
import com.kira.kmpbase.core.domain.usecase.home.RefreshContactsUseCase
import com.kira.kmpbase.core.monitoring.Monitoring
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.kira.kmpbase.core.ui.viewmodel.launchSafely

class HomeViewModel(
    private val observeContactsUseCase: ObserveContactsUseCase,
    private val refreshContactsUseCase: RefreshContactsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadContacts()
    }

    fun loadContacts() {
        launchSafely {
            observeContactsUseCase().collect { result ->
                _uiState.update { result.toUiState() }
            }
        }
    }

    fun refresh() {
        launchSafely {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = refreshContactsUseCase()) {
                is AppResult.Success -> _uiState.update { HomeUiState(contacts = result.data) }
                is AppResult.Error -> _uiState.update { result.toUiState() }
                AppResult.Loading -> Unit
            }
        }
    }

    fun testCrashlytics() {
        Monitoring.sendTestCrash()
    }
}
