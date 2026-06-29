package com.kira.kmpbase.feature.settings

import androidx.lifecycle.ViewModel
import com.kira.kmpbase.core.domain.usecase.auth.LogoutUseCase
import com.kira.kmpbase.core.ui.viewmodel.launchSafely

class SettingsViewModel(
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {

    fun logout() {
        launchSafely {
            logoutUseCase()
        }
    }
}
