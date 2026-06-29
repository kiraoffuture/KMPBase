package com.kira.kmpbase

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable

@Composable
internal expect fun BottomNavBar(
    content: @Composable RowScope.() -> Unit,
)
