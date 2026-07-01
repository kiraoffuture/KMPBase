package com.kira.kmpbase.core.monitoring

import co.touchlab.kermit.Logger
import co.touchlab.kermit.crashlytics.CrashlyticsLogWriter
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.crashlytics.crashlytics

internal actual fun configureMonitoring(platformContext: Any?) {
    Firebase.crashlytics.setCrashlyticsCollectionEnabled(true)
    Logger.addLogWriter(CrashlyticsLogWriter())
}

internal actual fun platformLog(message: String) {
    runCatching { Firebase.crashlytics.log(message) }
}

internal actual fun platformSetUserId(userId: String) {
    runCatching { Firebase.crashlytics.setUserId(userId) }
}

internal actual fun platformRecordException(throwable: Throwable) {
    runCatching { Firebase.crashlytics.recordException(throwable) }
}
