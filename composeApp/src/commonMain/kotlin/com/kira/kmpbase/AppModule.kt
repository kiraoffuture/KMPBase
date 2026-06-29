package com.kira.kmpbase

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::SessionViewModel)
}
