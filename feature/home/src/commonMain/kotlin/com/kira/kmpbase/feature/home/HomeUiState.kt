package com.kira.kmpbase.feature.home

import com.kira.kmpbase.core.domain.model.AppError
import com.kira.kmpbase.core.domain.model.AppResult
import com.kira.kmpbase.core.domain.model.Contact

data class HomeUiState(
    val contacts: List<Contact> = emptyList(),
    val isLoading: Boolean = false,
    val error: AppError? = null,
)

fun AppResult<List<Contact>>.toUiState(): HomeUiState {
    return when (this) {
        is AppResult.Loading -> HomeUiState(isLoading = true)
        is AppResult.Success -> HomeUiState(contacts = data)
        is AppResult.Error -> HomeUiState(error = error)
    }
}
