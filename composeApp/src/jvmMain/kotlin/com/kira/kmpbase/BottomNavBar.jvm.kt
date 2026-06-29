package com.kira.kmpbase

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable

@Composable
internal actual fun BottomNavBar(
    content: @Composable RowScope.() -> Unit,
) {
    NavigationBar(content = content)
}
