package com.kira.kmpbase.core.common

import co.touchlab.kermit.Logger

object AppLogger {
    const val TAG = "KMPLogTag"

    fun init() {
        Logger.setTag(TAG)
    }
}
