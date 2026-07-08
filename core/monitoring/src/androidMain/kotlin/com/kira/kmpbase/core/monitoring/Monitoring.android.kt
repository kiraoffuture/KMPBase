package com.kira.kmpbase.core.monitoring

import android.content.Context
import co.touchlab.kermit.Logger
import co.touchlab.kermit.crashlytics.CrashlyticsLogWriter
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.analytics.analytics
import dev.gitlive.firebase.crashlytics.crashlytics
import dev.gitlive.firebase.initialize

internal actual fun configureMonitoring(platformContext: Any?) {
    val context = platformContext as? Context
        ?: error("Monitoring.init requires Android Context")

    Firebase.initialize(context)
    Firebase.crashlytics.setCrashlyticsCollectionEnabled(true)
    Firebase.analytics.setAnalyticsCollectionEnabled(true)
    Logger.addLogWriter(CrashlyticsLogWriter())
}

internal actual fun platformLog(message: String) {
    runCatching { Firebase.crashlytics.log(message) }
}

internal actual fun platformSetUserId(userId: String) {
    runCatching { Firebase.crashlytics.setUserId(userId) }
    runCatching { Firebase.analytics.setUserId(userId) }
}

internal actual fun platformRecordException(throwable: Throwable) {
    runCatching { Firebase.crashlytics.recordException(throwable) }
}

internal actual fun platformLogEvent(name: String, params: Map<String, Any>) {
    runCatching { Firebase.analytics.logEvent(name, params) }
}

internal actual fun platformSetUserProperty(name: String, value: String) {
    runCatching { Firebase.analytics.setUserProperty(name, value) }
}
