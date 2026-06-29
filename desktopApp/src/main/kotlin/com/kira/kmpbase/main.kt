package com.kira.kmpbase

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    initApp()
    Window(
        onCloseRequest = ::exitApplication,
        title = "KMPBase",
    ) {
        App()
    }
}
