package com.plcoding.marky.core.data.note

import androidx.sqlite.SQLiteException
import app.cash.turbine.test
import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isTrue
import com.plcoding.marky.core.database.note.NoteEntity
import com.plcoding.marky.core.domain.note.Note
import com.plcoding.marky.core.domain.util.DataError
import com.plcoding.marky.core.domain.util.Result
import kotlinx.coroutines.test.runTest
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.Test
import kotlin.time.Instant

class RoomNoteDataSourceTest {

    private val dao = FakeNoteDao()
    private val dataSource = RoomNoteDataSource(dao)

    private fun note(id: Long = 0, title: String = "Ideas") = Note(
        id = id,
        title = title,
        content = "- first",
        createdAt = Instant.fromEpochMilliseconds(1_000),
        lastEditedAt = Instant.fromEpochMilliseconds(2_000),
        isPinned = false
    )

    @Test
    fun `inserting a new note returns the generated id`() = runTest {
        val result = dataSource.upsertNote(note(id = 0))

        assertThat(result).isEqualTo(Result.Success(1L))
    }

    @Test
    fun `updating an existing note returns its own id instead of the DAO marker`() = runTest {
        dao.upsertNote(note().toNoteEntity())
        dao.upsertNote(note().toNoteEntity())

        val result = dataSource.upsertNote(note(id = 2, title = "Renamed"))

        assertThat(result).isEqualTo(Result.Success(2L))
        assertThat(dao.notes.value.single { it.id == 2L }.title).isEqualTo("Renamed")
    }

    @Test
    fun `getting a stored note maps it to the domain model`() = runTest {
        dao.upsertNote(note().toNoteEntity())

        assertThat(dataSource.getNote(1)).isEqualTo(Result.Success(note(id = 1)))
    }

    @Test
    fun `getting a missing note fails with NOT_FOUND`() = runTest {
        assertThat(dataSource.getNote(7)).isEqualTo(Result.Error(DataError.Local.NOT_FOUND))
    }

    @Test
    fun `pinning a stored note succeeds`() = runTest {
        dao.upsertNote(note().toNoteEntity())

        assertThat(dataSource.setPinned(1, isPinned = true)).isEqualTo(Result.Success(Unit))
        assertThat(dao.notes.value.single().isPinned).isTrue()
    }

    @Test
    fun `pinning a missing note fails with NOT_FOUND`() = runTest {
        assertThat(dataSource.setPinned(7, isPinned = true))
            .isEqualTo(Result.Error(DataError.Local.NOT_FOUND))
    }

    @Test
    fun `deleting a stored note removes it`() = runTest {
        dao.upsertNote(note().toNoteEntity())

        assertThat(dataSource.deleteNote(1)).isEqualTo(Result.Success(Unit))
        assertThat(dao.notes.value).isEmpty()
    }

    @Test
    fun `deleting a missing note fails with NOT_FOUND`() = runTest {
        assertThat(dataSource.deleteNote(7)).isEqualTo(Result.Error(DataError.Local.NOT_FOUND))
    }

    @Test
    fun `observing notes maps every emission`() = runTest {
        dataSource.observeNotes().test {
            assertThat(awaitItem()).isEmpty()

            dao.upsertNote(note().toNoteEntity())

            assertThat(awaitItem()).containsExactly(note(id = 1))
        }
    }

    @Test
    fun `search passes a sanitized query to the DAO and maps the results`() = runTest {
        dao.searchResult = listOf(note(id = 3).toNoteEntity())

        val result = dataSource.searchNotes("Trip OR \"Lisbon\"")

        assertThat(dao.searchQueries).containsExactly("trip* or* lisbon*")
        assertThat(result).isEqualTo(Result.Success(listOf(note(id = 3))))
    }

    @Test
    fun `search without searchable characters returns nothing and skips the DAO`() = runTest {
        dao.failure = IllegalStateException("must not be called")

        val result = dataSource.searchNotes(" \"* ")

        assertThat(result).isEqualTo(Result.Success(emptyList()))
        assertThat(dao.searchQueries).isEmpty()
    }

    @Test
    fun `SQLite error maps to an error result instead of crashing`() = runTest {
        dao.failure = SQLiteException("Error code: 11, message: database disk image is malformed")

        assertThat(dataSource.getNote(1)).isEqualTo(Result.Error(DataError.Local.UNKNOWN))
    }

    @Test
    fun `non-SQLite exception maps to UNKNOWN`() = runTest {
        dao.failure = IllegalStateException("closed")

        assertThat(dataSource.deleteNote(1)).isEqualTo(Result.Error(DataError.Local.UNKNOWN))
    }

    @Test
    fun `cancellation is rethrown instead of mapped to an error`() = runTest {
        dao.failure = CancellationException("cancelled")

        assertFailure { dataSource.setPinned(1, isPinned = true) }
            .isInstanceOf<CancellationException>()
    }
}
