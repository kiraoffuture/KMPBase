package com.kira.kmpbase

import com.kira.kmpbase.core.di.initKoin
import com.kira.kmpbase.feature.auth.authModule
import com.kira.kmpbase.feature.home.homeModule
import com.kira.kmpbase.feature.settings.settingsModule

fun initApp() {
    initKoin {
        modules(appModule, homeModule, authModule, settingsModule)
    }
}
