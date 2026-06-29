package com.kira.kmpbase

import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController() = ComposeUIViewController(
    configure = {
        initApp()
    },
) {
    App()
}
