package com.kira.kmpbase

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform