package com.plcoding.marky.core.database.note

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.FtsOptions

/**
 * Full-text index over the title and the note's plain text. Indexing the raw Markdown
 * would split words at inline syntax, so `wo*rd*` could never match "word".
 */
@Fts4(
    contentEntity = NoteEntity::class,
    tokenizer = FtsOptions.TOKENIZER_UNICODE61
)
@Entity(tableName = "notes_fts")
data class NoteFtsEntity(
    val title: String,
    val searchText: String
)
