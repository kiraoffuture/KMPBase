package com.kira.kmpbase.core.di

import com.kira.kmpbase.core.common.storage.IosSecureStorage
import com.kira.kmpbase.core.common.storage.SecureStorage
import org.koin.dsl.module

actual val platformSecureStorageModule = module {
    single<SecureStorage> { IosSecureStorage() }
}
