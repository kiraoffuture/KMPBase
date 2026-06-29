package com.kira.kmpbase

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.kira.kmpbase.core.ui.theme.AppTheme

@Composable
@Preview
fun App() {
    AppTheme {
        RootNavHost()
    }
}
