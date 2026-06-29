package com.kira.kmpbase.core.ui.viewmodel

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import org.koin.compose.getKoin

@Composable
inline fun <reified T : ViewModel> koinViewModel(): T {
    val koin = getKoin()
    return viewModel { koin.get<T>() }
}
