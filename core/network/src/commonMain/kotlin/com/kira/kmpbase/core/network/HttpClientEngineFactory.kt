package com.kira.kmpbase.core.network

import io.ktor.client.engine.HttpClientEngine

expect fun createHttpClientEngine(): HttpClientEngine
