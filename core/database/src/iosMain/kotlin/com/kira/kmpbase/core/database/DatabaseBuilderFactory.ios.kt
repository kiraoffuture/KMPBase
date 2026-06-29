package com.kira.kmpbase.core.database

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSError
import platform.Foundation.NSFileManager
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSUserDomainMask

actual class DatabaseBuilderFactory {
    actual fun create(): AppDatabase {
        val databasePath = "${documentDirectory()}/$DATABASE_NAME"
        return Room.databaseBuilder<AppDatabase>(name = databasePath)
            .setDriver(BundledSQLiteDriver())
            .build()
    }

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    private fun documentDirectory(): String = memScoped {
        val errorPtr = alloc<ObjCObjectVar<NSError?>>()
        val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = errorPtr.ptr,
        )
        if (documentDirectory != null) {
            return@memScoped requireNotNull(documentDirectory.path) {
                "Could not resolve iOS document directory path."
            }
        }

        val fallbackDirectory = NSTemporaryDirectory().trimEnd('/')
        if (fallbackDirectory.isNotEmpty()) {
            return@memScoped fallbackDirectory
        }

        val errorMessage = errorPtr.value?.localizedDescription ?: "Unknown error"
        error("Could not resolve iOS document directory. Error: $errorMessage")
    }

    private companion object {
        const val DATABASE_NAME = "kmpbase.db"
    }
}
