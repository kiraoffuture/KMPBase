package com.kira.kmpbase.core.database

expect class DatabaseBuilderFactory {
    fun create(): AppDatabase
}

fun createDatabase(factory: DatabaseBuilderFactory): AppDatabase = factory.create()
