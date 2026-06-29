package com.kira.kmpbase

import androidx.lifecycle.ViewModel
import com.kira.kmpbase.core.domain.usecase.auth.ObserveSessionUseCase
import com.kira.kmpbase.core.domain.usecase.auth.RefreshSessionUseCase
import kotlinx.coroutines.flow.StateFlow
import com.kira.kmpbase.core.ui.viewmodel.launchSafely

class SessionViewModel(
    observeSessionUseCase: ObserveSessionUseCase,
    private val refreshSessionUseCase: RefreshSessionUseCase,
) : ViewModel() {

    val sessionState: StateFlow<Boolean?> = observeSessionUseCase()

    init {
        launchSafely {
            refreshSessionUseCase()
        }
    }
}
