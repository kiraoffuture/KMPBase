package com.kira.kmpbase.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

@Serializable
sealed class AppDestination {
    @Serializable
    data object Home : AppDestination()

    @Serializable
    data object Settings : AppDestination()
}

enum class BottomNavItem(
    val icon: ImageVector,
    val destination: AppDestination,
) {
    Home(Icons.Default.Home, AppDestination.Home),
    Settings(Icons.Default.Settings, AppDestination.Settings),
}
