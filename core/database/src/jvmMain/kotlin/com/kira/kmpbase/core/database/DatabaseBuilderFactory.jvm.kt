package com.kira.kmpbase.core.database

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlin.io.path.absolutePathString
import kotlin.io.path.createTempFile

actual class DatabaseBuilderFactory {
    actual fun create(): AppDatabase {
        val databasePath = createTempFile(prefix = "kmpbase", suffix = ".db").apply {
            toFile().deleteOnExit()
        }.absolutePathString()
        return Room.databaseBuilder<AppDatabase>(
            name = databasePath,
            factory = AppDatabaseConstructor::initialize,
        )
            .setDriver(BundledSQLiteDriver())
            .build()
    }
}
