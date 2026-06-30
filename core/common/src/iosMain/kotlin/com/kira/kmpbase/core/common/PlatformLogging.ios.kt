package com.kira.kmpbase.core.common

import co.touchlab.kermit.Logger
import co.touchlab.kermit.NSLogWriter

internal actual fun configurePlatformLogging() {
    Logger.setLogWriters(NSLogWriter())
}
