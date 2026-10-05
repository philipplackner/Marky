package com.plcoding.marky.core.data.note

import com.plcoding.marky.core.data.util.safeDbCall
import com.plcoding.marky.core.database.note.NoteDao
import com.plcoding.marky.core.domain.note.Note
import com.plcoding.marky.core.domain.note.NoteLocalDataSource
import com.plcoding.marky.core.domain.util.DataError
import com.plcoding.marky.core.domain.util.EmptyResult
import com.plcoding.marky.core.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomNoteDataSource(
    private val noteDao: NoteDao
) : NoteLocalDataSource {

    override fun observeNotes(): Flow<List<Note>> {
        return noteDao.observeNotes().map { entities ->
            entities.map { it.toNote() }
        }
    }

    override suspend fun getNote(id: Long): Result<Note, DataError.Local> {
        return safeDbCall {
            val entity = noteDao.getNote(id)
            if (entity != null) {
                Result.Success(entity.toNote())
            } else {
                Result.Error(DataError.Local.NOT_FOUND)
            }
        }
    }

    override suspend fun upsertNote(note: Note): Result<Long, DataError.Local> {
        return safeDbCall {
            val rowId = noteDao.upsertNote(note.toNoteEntity())
            Result.Success(if (rowId == UPDATED_ROW_ID) note.id else rowId)
        }
    }

    override suspend fun setPinned(id: Long, isPinned: Boolean): EmptyResult<DataError.Local> {
        return safeDbCall {
            noteDao.updatePinned(id, isPinned).toEmptyResult()
        }
    }

    override suspend fun deleteNote(id: Long): EmptyResult<DataError.Local> {
        return safeDbCall {
            noteDao.deleteNote(id).toEmptyResult()
        }
    }

    override suspend fun searchNotes(query: String): Result<List<Note>, DataError.Local> {
        val ftsQuery = query.toFtsQuery() ?: return Result.Success(emptyList())
        return safeDbCall {
            Result.Success(noteDao.searchNotes(ftsQuery).map { it.toNote() })
        }
    }

    private fun Int.toEmptyResult(): EmptyResult<DataError.Local> {
        return if (this > 0) Result.Success(Unit) else Result.Error(DataError.Local.NOT_FOUND)
    }

    private companion object {
        const val UPDATED_ROW_ID = -1L
    }
}
