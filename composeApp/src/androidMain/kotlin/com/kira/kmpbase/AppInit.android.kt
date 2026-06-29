package com.kira.kmpbase

import android.content.Context
import com.kira.kmpbase.core.di.initKoin
import com.kira.kmpbase.feature.auth.authModule
import com.kira.kmpbase.feature.home.homeModule
import com.kira.kmpbase.feature.settings.settingsModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext

fun initAndroidApp(context: Context) {
    if (GlobalContext.getOrNull() == null) {
        initKoin {
            androidContext(context.applicationContext)
            modules(appModule, homeModule, authModule, settingsModule)
        }
    }
}
