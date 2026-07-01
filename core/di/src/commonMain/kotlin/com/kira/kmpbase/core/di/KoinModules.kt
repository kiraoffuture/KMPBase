package com.kira.kmpbase.core.di

import com.kira.kmpbase.core.common.DefaultDispatcherProvider
import com.kira.kmpbase.core.common.DispatcherProvider
import com.kira.kmpbase.core.data.AuthRepositoryImpl
import com.kira.kmpbase.core.data.HomeRepositoryImpl
import com.kira.kmpbase.core.data.auth.AuthLocalKeys
import com.kira.kmpbase.core.data.local.LocalPreferences
import com.kira.kmpbase.core.data.local.LocalPreferencesImpl
import com.kira.kmpbase.core.data.local.SecureLocalPreferences
import com.kira.kmpbase.core.data.local.SecureLocalPreferencesImpl
import com.kira.kmpbase.core.database.CacheDao
import com.kira.kmpbase.core.database.DatabaseBuilderFactory
import com.kira.kmpbase.core.database.createDatabase
import com.kira.kmpbase.core.domain.repository.AuthRepository
import com.kira.kmpbase.core.domain.repository.HomeRepository
import com.kira.kmpbase.core.domain.usecase.auth.LoginUseCase
import com.kira.kmpbase.core.domain.usecase.auth.LogoutUseCase
import com.kira.kmpbase.core.domain.usecase.auth.ObserveSessionUseCase
import com.kira.kmpbase.core.domain.usecase.auth.RefreshSessionUseCase
import com.kira.kmpbase.core.domain.usecase.home.ObserveContactsUseCase
import com.kira.kmpbase.core.domain.usecase.home.RefreshContactsUseCase
import com.kira.kmpbase.core.network.createAuthApiService
import com.kira.kmpbase.core.network.createHomeApiService
import com.kira.kmpbase.core.network.createHttpClient
import com.kira.kmpbase.core.network.createHttpClientEngine
import com.russhwolf.settings.Settings
import org.koin.core.module.Module
import org.koin.dsl.module

val coreModule = module {
    single<DispatcherProvider> { DefaultDispatcherProvider() }
    single<Settings> { Settings() }
}

val networkModule = module {
    single { createHttpClientEngine() }
    single {
        createHttpClient(
            engine = get(),
            authTokenProvider = {
                get<SecureLocalPreferences>().getString(AuthLocalKeys.AUTH_TOKEN).orEmpty()
            },
        )
    }
    single { createAuthApiService(get()) }
    single { createHomeApiService(get()) }
}

val databaseModule = module {
    single { createDatabase(get<DatabaseBuilderFactory>()) }
    single { CacheDao(get()) }
}

val dataModule = module {
    single<LocalPreferences> { LocalPreferencesImpl(get()) }
    single<SecureLocalPreferences> { SecureLocalPreferencesImpl(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<HomeRepository> { HomeRepositoryImpl(get(), get(), get()) }
}

val domainModule = module {
    factory { LoginUseCase(get()) }
    factory { LogoutUseCase(get()) }
    factory { ObserveSessionUseCase(get()) }
    factory { RefreshSessionUseCase(get()) }
    factory { ObserveContactsUseCase(get()) }
    factory { RefreshContactsUseCase(get()) }
}

val coreModules: List<Module> = listOf(
    coreModule,
    platformSecureStorageModule,
    networkModule,
    platformDatabaseModule,
    databaseModule,
    dataModule,
    domainModule,
)
