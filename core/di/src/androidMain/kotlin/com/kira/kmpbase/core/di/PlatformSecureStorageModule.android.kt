package com.kira.kmpbase.core.di

import com.kira.kmpbase.core.common.storage.AndroidSecureStorage
import com.kira.kmpbase.core.common.storage.SecureStorage
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val platformSecureStorageModule = module {
    single<SecureStorage> { AndroidSecureStorage(androidContext()) }
}
