package com.plcoding.marky.feature.notes.presentation

import com.plcoding.marky.core.domain.note.Note
import com.plcoding.marky.core.domain.note.NoteLocalDataSource
import com.plcoding.marky.core.domain.util.DataError
import com.plcoding.marky.core.domain.util.EmptyResult
import com.plcoding.marky.core.domain.util.Result
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeNoteLocalDataSource : NoteLocalDataSource {

    private val notesFlow = MutableStateFlow<Map<Long, Note>>(emptyMap())
    private var nextId = 1L

    val notes: Map<Long, Note>
        get() = notesFlow.value

    var upsertError: DataError.Local? = null
    var getError: DataError.Local? = null
    var pinError: DataError.Local? = null
    var deleteError: DataError.Local? = null

    /** When set, upserts suspend until it completes. */
    var upsertGate: CompletableDeferred<Unit>? = null

    var upsertCount = 0
        private set
    val deletedIds = mutableListOf<Long>()

    fun add(note: Note): Note {
        val stored = if (note.id == 0L) note.copy(id = nextId++) else note
        nextId = maxOf(nextId, stored.id + 1)
        notesFlow.value += stored.id to stored
        return stored
    }

    override fun observeNotes(): Flow<List<Note>> = notesFlow.map { notes ->
        notes.values.sortedWith(compareByDescending<Note> { it.isPinned }.thenByDescending { it.lastEditedAt })
    }

    override suspend fun getNote(id: Long): Result<Note, DataError.Local> {
        getError?.let { return Result.Error(it) }
        return notes[id]?.let { Result.Success(it) } ?: Result.Error(DataError.Local.NOT_FOUND)
    }

    override suspend fun upsertNote(note: Note): Result<Long, DataError.Local> {
        upsertGate?.await()
        upsertCount++
        upsertError?.let { return Result.Error(it) }
        return Result.Success(add(note).id)
    }

    override suspend fun setPinned(id: Long, isPinned: Boolean): EmptyResult<DataError.Local> {
        pinError?.let { return Result.Error(it) }
        val note = notes[id] ?: return Result.Error(DataError.Local.NOT_FOUND)
        notesFlow.value += id to note.copy(isPinned = isPinned)
        return Result.Success(Unit)
    }

    override suspend fun deleteNote(id: Long): EmptyResult<DataError.Local> {
        deleteError?.let { return Result.Error(it) }
        deletedIds += id
        notesFlow.value -= id
        return Result.Success(Unit)
    }

    override suspend fun searchNotes(query: String): Result<List<Note>, DataError.Local> =
        Result.Success(emptyList())
}
