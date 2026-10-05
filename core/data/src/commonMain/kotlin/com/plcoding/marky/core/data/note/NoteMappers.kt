package com.plcoding.marky.core.data.note

import com.plcoding.marky.core.database.note.NoteEntity
import com.plcoding.marky.core.domain.note.Note
import kotlin.time.Instant

fun NoteEntity.toNote(): Note {
    return Note(
        id = id,
        title = title,
        content = content,
        createdAt = Instant.fromEpochMilliseconds(createdAt),
        lastEditedAt = Instant.fromEpochMilliseconds(lastEditedAt),
        isPinned = isPinned
    )
}

fun Note.toNoteEntity(): NoteEntity {
    return NoteEntity(
        id = id,
        title = title,
        content = content,
        createdAt = createdAt.toEpochMilliseconds(),
        lastEditedAt = lastEditedAt.toEpochMilliseconds(),
        isPinned = isPinned
    )
}
