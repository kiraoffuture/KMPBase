package com.kira.kmpbase.core.common

import co.touchlab.kermit.Logger

object AppLogger {
    const val TAG = "KMPLogTag"

    fun init() {
        configurePlatformLogging()
        Logger.setTag(TAG)
    }
}

internal expect fun configurePlatformLogging()
