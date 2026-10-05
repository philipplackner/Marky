package com.plcoding.marky.core.database.note

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes ORDER BY isPinned DESC, lastEditedAt DESC, id DESC")
    fun observeNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNote(id: Long): NoteEntity?

    /**
     * Inserts [note] if its id is 0 or unknown, otherwise updates it.
     *
     * @return the new row id on insert, or -1 on update.
     */
    @Upsert
    suspend fun upsertNote(note: NoteEntity): Long

    @Query("UPDATE notes SET isPinned = :isPinned WHERE id = :id")
    suspend fun updatePinned(id: Long, isPinned: Boolean): Int

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNote(id: Long): Int

    /**
     * @param ftsQuery an FTS4 MATCH expression, not raw user input.
     */
    @Query(
        """
        SELECT notes.* FROM notes
        JOIN notes_fts ON notes.id = notes_fts.rowid
        WHERE notes_fts MATCH :ftsQuery
        ORDER BY notes.lastEditedAt DESC, notes.id DESC
        """
    )
    suspend fun searchNotes(ftsQuery: String): List<NoteEntity>
}
