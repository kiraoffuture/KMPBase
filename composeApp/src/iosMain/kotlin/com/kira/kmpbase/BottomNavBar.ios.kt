package com.kira.kmpbase

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
internal actual fun BottomNavBar(
    content: @Composable RowScope.() -> Unit,
) {
    val containerColor = NavigationBarDefaults.containerColor
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(containerColor),
        contentAlignment = Alignment.TopCenter,
    ) {
        NavigationBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = (bottomInset - 18.dp).coerceAtLeast(0.dp)),
            containerColor = Color.Transparent,
            windowInsets = WindowInsets(0),
            content = content,
        )
    }
}
