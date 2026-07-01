package com.kira.kmpbase.core.monitoring

object Monitoring {
    fun init(platformContext: Any? = null) {
        configureMonitoring(platformContext)
    }

    fun log(message: String) {
        platformLog(message)
    }

    fun setUserId(userId: String) {
        platformSetUserId(userId)
    }

    fun recordException(throwable: Throwable) {
        platformRecordException(throwable)
    }

    fun sendTestCrash(): Nothing {
        platformLog("Crashlytics fatal test triggered from Home")
        throw RuntimeException("KMP Base Crashlytics test (fatal)")
    }
}

internal expect fun configureMonitoring(platformContext: Any?)

internal expect fun platformLog(message: String)

internal expect fun platformSetUserId(userId: String)

internal expect fun platformRecordException(throwable: Throwable)
