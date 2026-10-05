package com.plcoding.marky.core.data.note

import com.plcoding.marky.core.database.note.NoteDao
import com.plcoding.marky.core.database.note.NoteEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FakeNoteDao : NoteDao {

    val notes = MutableStateFlow<List<NoteEntity>>(emptyList())
    val searchQueries = mutableListOf<String>()
    var searchResult = emptyList<NoteEntity>()
    var failure: Throwable? = null

    private var nextId = 1L

    override fun observeNotes(): Flow<List<NoteEntity>> = notes

    override suspend fun getNote(id: Long): NoteEntity? {
        failure?.let { throw it }
        return notes.value.find { it.id == id }
    }

    override suspend fun upsertNote(note: NoteEntity): Long {
        failure?.let { throw it }
        if (note.id != 0L && notes.value.any { it.id == note.id }) {
            notes.update { list -> list.map { if (it.id == note.id) note else it } }
            return -1
        }
        val id = if (note.id == 0L) nextId++ else note.id
        notes.update { it + note.copy(id = id) }
        return id
    }

    override suspend fun updatePinned(id: Long, isPinned: Boolean): Int {
        failure?.let { throw it }
        val exists = notes.value.any { it.id == id }
        notes.update { list -> list.map { if (it.id == id) it.copy(isPinned = isPinned) else it } }
        return if (exists) 1 else 0
    }

    override suspend fun deleteNote(id: Long): Int {
        failure?.let { throw it }
        val before = notes.value.size
        notes.update { list -> list.filterNot { it.id == id } }
        return before - notes.value.size
    }

    override suspend fun searchNotes(ftsQuery: String): List<NoteEntity> {
        failure?.let { throw it }
        searchQueries += ftsQuery
        return searchResult
    }
}
