package com.kira.kmpbase.core.network

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.java.Java

actual fun createHttpClientEngine(): HttpClientEngine = Java.create()
