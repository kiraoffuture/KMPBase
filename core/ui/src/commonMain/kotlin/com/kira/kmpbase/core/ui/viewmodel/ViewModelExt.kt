package com.kira.kmpbase.core.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.launch

fun ViewModel.launchSafely(
    block: suspend () -> Unit,
) {
    viewModelScope.launch {
        try {
            block()
        } catch (throwable: Throwable) {
            Logger.e(throwable) { "[Coroutine] Unhandled error in ${this@launchSafely::class.simpleName}" }
        }
    }
}
