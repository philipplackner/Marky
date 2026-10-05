package com.plcoding.marky.core.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.plcoding.marky.core.database.note.NoteDao
import com.plcoding.marky.core.database.note.NoteEntity
import com.plcoding.marky.core.database.note.NoteFtsEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(
    entities = [NoteEntity::class, NoteFtsEntity::class],
    version = 1
)
@ConstructedBy(MarkyDatabaseConstructor::class)
abstract class MarkyDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}

@Suppress("KotlinNoActualForExpect")
expect object MarkyDatabaseConstructor : RoomDatabaseConstructor<MarkyDatabase> {
    override fun initialize(): MarkyDatabase
}

internal const val DATABASE_FILE_NAME = "marky.db"

fun createMarkyDatabase(builder: RoomDatabase.Builder<MarkyDatabase>): MarkyDatabase {
    return builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}
