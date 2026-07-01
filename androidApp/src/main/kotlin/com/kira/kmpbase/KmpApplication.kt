package com.kira.kmpbase

import android.app.Application
import com.kira.kmpbase.core.monitoring.Monitoring

class KmpApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Monitoring.init(applicationContext)
    }
}
