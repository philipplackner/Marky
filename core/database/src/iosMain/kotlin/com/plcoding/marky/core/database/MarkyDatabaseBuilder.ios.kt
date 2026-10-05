package com.plcoding.marky.core.database

import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

fun markyDatabaseBuilder(): RoomDatabase.Builder<MarkyDatabase> {
    return Room.databaseBuilder<MarkyDatabase>(
        name = documentDirectory() + "/$DATABASE_FILE_NAME"
    )
}

@OptIn(ExperimentalForeignApi::class)
private fun documentDirectory(): String {
    val directory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null
    )
    return requireNotNull(directory?.path)
}
