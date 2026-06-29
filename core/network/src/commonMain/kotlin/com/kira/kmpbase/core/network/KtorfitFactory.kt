package com.kira.kmpbase.core.network

import com.kira.kmpbase.core.common.NetworkConfig
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient

fun createKtorfit(httpClient: HttpClient): Ktorfit {
    return Ktorfit.Builder()
        .baseUrl(NetworkConfig.BASE_URL)
        .httpClient(httpClient)
        .build()
}
