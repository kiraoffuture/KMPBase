package com.kira.kmpbase

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kira.kmpbase.core.ui.viewmodel.koinViewModel
import com.kira.kmpbase.feature.auth.LoginScreen

@Composable
fun RootNavHost(
    modifier: Modifier = Modifier,
) {
    val sessionViewModel: SessionViewModel = koinViewModel()
    val sessionState by sessionViewModel.sessionState.collectAsState()

    when (sessionState) {
        null -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }
        false -> {
            LoginScreen(
                onLoginSuccess = { },
                modifier = modifier,
            )
        }
        true -> {
            AppNavHost(modifier = modifier)
        }
    }
}
