package com.kira.kmpbase.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage

@Composable
fun ContactAvatar(
    url: String?,
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
) {
    val shape = CircleShape
    val sizedModifier = modifier.size(size).clip(shape)

    if (url.isNullOrBlank()) {
        AvatarFallback(name = name, modifier = sizedModifier)
        return
    }

    SubcomposeAsyncImage(
        model = url,
        contentDescription = name,
        modifier = sizedModifier,
        contentScale = ContentScale.Crop,
        loading = {
            AvatarFallback(name = name, modifier = Modifier.fillMaxSize())
        },
        error = {
            AvatarFallback(name = name, modifier = Modifier.fillMaxSize())
        },
    )
}

@Composable
private fun AvatarFallback(
    name: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = name.firstOrNull()?.uppercaseChar()?.toString().orEmpty(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    }
}
