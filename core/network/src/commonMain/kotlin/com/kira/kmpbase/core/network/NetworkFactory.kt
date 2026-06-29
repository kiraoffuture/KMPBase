package com.kira.kmpbase.core.network

import io.ktor.client.HttpClient

fun createAuthApiService(httpClient: HttpClient): AuthApiService {
    return AuthApiService(createKtorfit(httpClient).createAuthApi())
}

fun createHomeApiService(httpClient: HttpClient): HomeApiService {
    return HomeApiService(createKtorfit(httpClient).createHomeApi())
}
