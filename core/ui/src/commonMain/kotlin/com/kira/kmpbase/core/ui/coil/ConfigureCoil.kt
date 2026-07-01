package com.kira.kmpbase.core.ui.coil

import androidx.compose.runtime.Composable
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory

@Composable
fun ConfigureCoil() {
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context).build()
    }
}
