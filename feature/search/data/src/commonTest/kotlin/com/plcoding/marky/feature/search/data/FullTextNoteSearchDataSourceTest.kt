package com.plcoding.marky.feature.search.data

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.plcoding.marky.core.domain.note.Note
import com.plcoding.marky.core.domain.util.DataError
import com.plcoding.marky.core.domain.util.Result
import com.plcoding.marky.feature.search.domain.SearchResults
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.Instant

class FullTextNoteSearchDataSourceTest {

    private val notes = FakeNoteLocalDataSource()
    private val dataSource = FullTextNoteSearchDataSource(notes, UnconfinedTestDispatcher())

    private fun note(id: Long, title: String, content: String) = Note(
        id = id,
        title = title,
        content = content,
        createdAt = Instant.fromEpochMilliseconds(0),
        lastEditedAt = Instant.fromEpochMilliseconds(id),
        isPinned = false,
    )

    private fun Result<SearchResults, DataError.Local>.data() = (this as Result.Success).data

    @Test
    fun `markdown syntax is stripped before matching and in snippets`() = runTest {
        notes.searchResult = Result.Success(listOf(note(1, "Plans", "## Trip\n\nThe **Lisbon** offsite")))
        dataSource.search("lisbon").test {
            val result = awaitItem().data().inText.single()
            assertThat(result.snippet.text).isEqualTo("Trip The Lisbon offsite")
            assertThat(result.snippet.matches.map { result.snippet.text.substring(it) }).containsExactly("Lisbon")
        }
    }

    @Test
    fun `index hits only inside a link url are dropped`() = runTest {
        notes.searchResult = Result.Success(listOf(note(1, "Links", "[Tram 28](https://carris.pt)")))
        dataSource.search("carris").test {
            assertThat(awaitItem().data().isEmpty).isEqualTo(true)
        }
    }

    @Test
    fun `results refresh when notes change`() = runTest {
        notes.searchResult = Result.Success(emptyList())
        dataSource.search("lisbon").test {
            assertThat(awaitItem().data().isEmpty).isEqualTo(true)
            val lisbon = note(1, "Lisbon", "")
            notes.searchResult = Result.Success(listOf(lisbon))
            notes.notes.value = listOf(lisbon)
            assertThat(awaitItem().data().inTitles.map { it.noteId }).containsExactly(1L)
        }
        assertThat(notes.searchQueries).containsExactly("lisbon", "lisbon")
    }

    @Test
    fun `database errors are passed on`() = runTest {
        notes.searchResult = Result.Error(DataError.Local.UNKNOWN)
        dataSource.search("lisbon").test {
            assertThat(awaitItem()).isInstanceOf<Result.Error<DataError.Local>>()
        }
    }
}
