package com.plcoding.marky.core.database.note

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "notes",
    indices = [Index("isPinned", "lastEditedAt")]
)
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val createdAt: Long,
    val lastEditedAt: Long,
    val isPinned: Boolean
)
