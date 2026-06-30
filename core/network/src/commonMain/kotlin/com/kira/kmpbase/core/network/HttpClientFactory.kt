package com.kira.kmpbase.core.network

import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger as KtorLogger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.LoggingFormat
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private val ktorHttpLogger = object : KtorLogger {
    override fun log(message: String) {
        Logger.d { "[Http] $message" }
    }
}

fun createHttpClient(
    engine: HttpClientEngine,
    authTokenProvider: suspend () -> String = { "" },
    httpLogLevel: LogLevel = LogLevel.BODY,
): HttpClient {
    val authPlugin = createClientPlugin("AuthInterceptor") {
        onRequest { request, _ ->
            val token = authTokenProvider()
            if (token.isNotBlank()) {
                request.headers.append(HttpHeaders.Authorization, "Bearer $token")
            }
        }
    }

    return HttpClient(engine) {
        install(authPlugin)
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    prettyPrint = true
                },
            )
        }
        defaultRequest {
            contentType(ContentType.Application.Json)
        }
        install(Logging) {
            logger = ktorHttpLogger
            level = httpLogLevel
            format = LoggingFormat.OkHttp
        }
    }
}
