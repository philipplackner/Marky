package com.plcoding.marky.core.domain.note

import com.plcoding.marky.core.domain.util.DataError
import com.plcoding.marky.core.domain.util.EmptyResult
import com.plcoding.marky.core.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface NoteLocalDataSource {

    /**
     * Emits all notes: pinned notes first, each group sorted by last edited, newest first.
     */
    fun observeNotes(): Flow<List<Note>>

    suspend fun getNote(id: Long): Result<Note, DataError.Local>

    /**
     * Inserts [note] when its id is 0, otherwise replaces the stored note with the same id.
     *
     * @return the id of the stored note.
     */
    suspend fun upsertNote(note: Note): Result<Long, DataError.Local>

    /**
     * Changes only the pinned flag. The note's last edited time stays the same.
     */
    suspend fun setPinned(id: Long, isPinned: Boolean): EmptyResult<DataError.Local>

    suspend fun deleteNote(id: Long): EmptyResult<DataError.Local>

    /**
     * Full-text search over titles and text. Every word in [query] has to match the start of
     * a word in the note. Results are sorted by last edited, newest first, without ranking.
     */
    suspend fun searchNotes(query: String): Result<List<Note>, DataError.Local>
}
