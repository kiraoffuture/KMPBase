package com.kira.kmpbase.core.monitoring

internal actual fun configureMonitoring(platformContext: Any?) = Unit

internal actual fun platformLog(message: String) = Unit

internal actual fun platformSetUserId(userId: String) = Unit

internal actual fun platformRecordException(throwable: Throwable) = Unit

internal actual fun platformLogEvent(name: String, params: Map<String, Any>) = Unit

internal actual fun platformSetUserProperty(name: String, value: String) = Unit
