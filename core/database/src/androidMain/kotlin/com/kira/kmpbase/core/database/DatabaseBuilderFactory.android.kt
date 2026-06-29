package com.kira.kmpbase.core.database

import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

actual class DatabaseBuilderFactory(
    private val context: Context,
) {
    actual fun create(): AppDatabase {
        val databasePath = context.getDatabasePath(DATABASE_NAME).absolutePath
        return Room.databaseBuilder<AppDatabase>(
            context = context,
            name = databasePath,
        )
            .setDriver(BundledSQLiteDriver())
            .build()
    }

    private companion object {
        const val DATABASE_NAME = "kmpbase.db"
    }
}
