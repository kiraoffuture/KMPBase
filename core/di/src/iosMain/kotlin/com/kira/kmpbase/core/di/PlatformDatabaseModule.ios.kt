package com.kira.kmpbase.core.di

import com.kira.kmpbase.core.database.DatabaseBuilderFactory
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformDatabaseModule: Module = module {
    single { DatabaseBuilderFactory() }
}
