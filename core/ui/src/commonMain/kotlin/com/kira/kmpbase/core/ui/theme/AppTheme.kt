package com.kira.kmpbase.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val AppLightColorScheme = lightColorScheme(
    primary = AppColors.Primary,
    onPrimary = AppColors.OnPrimary,
    secondary = AppColors.Secondary,
    onSecondary = AppColors.OnPrimary,
    background = AppColors.Background,
    onBackground = AppColors.OnBackground,
    surface = AppColors.Surface,
    onSurface = AppColors.OnSurface,
    error = AppColors.Error,
    outline = AppColors.Outline,
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppLightColorScheme,
        typography = MaterialTheme.typography.copy(
            headlineSmall = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp),
            titleMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 16.sp),
            bodyMedium = TextStyle(fontSize = 14.sp),
        ),
        content = content,
    )
}
