package com.plcoding.marky.feature.search.data

import com.plcoding.marky.core.domain.note.Note
import com.plcoding.marky.core.domain.note.NoteLocalDataSource
import com.plcoding.marky.core.domain.util.DataError
import com.plcoding.marky.core.domain.util.EmptyResult
import com.plcoding.marky.core.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Its full-text search returns [searchResult] as is, like an index that only narrows candidates down.
 */
class FakeNoteLocalDataSource : NoteLocalDataSource {

    val notes = MutableStateFlow<List<Note>>(emptyList())
    var searchResult: Result<List<Note>, DataError.Local> = Result.Success(emptyList())
    val searchQueries = mutableListOf<String>()

    override fun observeNotes(): Flow<List<Note>> = notes

    override suspend fun getNote(id: Long): Result<Note, DataError.Local> =
        notes.value.find { it.id == id }?.let { Result.Success(it) } ?: Result.Error(DataError.Local.NOT_FOUND)

    override suspend fun upsertNote(note: Note): Result<Long, DataError.Local> = error("Not used")

    override suspend fun setPinned(id: Long, isPinned: Boolean): EmptyResult<DataError.Local> = error("Not used")

    override suspend fun deleteNote(id: Long): EmptyResult<DataError.Local> = error("Not used")

    override suspend fun searchNotes(query: String): Result<List<Note>, DataError.Local> {
        searchQueries += query
        return searchResult
    }
}
