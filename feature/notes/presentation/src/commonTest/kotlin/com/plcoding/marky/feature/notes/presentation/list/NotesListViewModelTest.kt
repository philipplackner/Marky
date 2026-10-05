package com.plcoding.marky.feature.notes.presentation.list

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.plcoding.marky.core.domain.note.Note
import com.plcoding.marky.feature.notes.domain.TimeSinceEdit
import com.plcoding.marky.feature.notes.presentation.FakeNoteLocalDataSource
import com.plcoding.marky.feature.notes.presentation.TestClock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.TimeZone
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class NotesListViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val dataSource = FakeNoteLocalDataSource()
    private val clock = TestClock()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = NotesListViewModel(dataSource, clock, TimeZone.UTC, testDispatcher)

    private fun note(title: String, content: String = "", editedAgo: Duration = 1.hours, isPinned: Boolean = false) =
        dataSource.add(
            Note(
                id = 0,
                title = title,
                content = content,
                createdAt = clock.now - editedAgo,
                lastEditedAt = clock.now - editedAgo,
                isPinned = isPinned,
            ),
        )

    @Test
    fun `no notes gives the empty state once loaded`() = runTest(testDispatcher) {
        createViewModel().state.test {
            val state = awaitItem()
            assertThat(state.isLoading).isFalse()
            assertThat(state.isEmpty).isTrue()
        }
    }

    @Test
    fun `pinned and recent notes are split and keep their order`() = runTest(testDispatcher) {
        note("Old pin", editedAgo = 3.hours, isPinned = true)
        note("New pin", editedAgo = 1.hours, isPinned = true)
        note("Older", editedAgo = 5.hours)
        note("Newer", editedAgo = 2.minutes)
        createViewModel().state.test {
            val state = awaitItem()
            assertThat(state.pinnedNotes.map { it.title }).containsExactly("New pin", "Old pin")
            assertThat(state.recentNotes.map { it.title }).containsExactly("Newer", "Older")
            assertThat(state.isEmpty).isFalse()
        }
    }

    @Test
    fun `blank title and text become null for untitled and no text yet`() = runTest(testDispatcher) {
        note(title = "  ", content = "- [ ] \n\n#")
        createViewModel().state.test {
            val ui = awaitItem().recentNotes.single()
            assertThat(ui.title).isEqualTo(null)
            assertThat(ui.preview).isEqualTo(null)
        }
    }

    @Test
    fun `preview has markdown removed`() = runTest(testDispatcher) {
        note(title = "Trip", content = "## Packing\n- [x] **Passport**")
        createViewModel().state.test {
            assertThat(awaitItem().recentNotes.single().preview).isEqualTo("Packing Passport")
        }
    }

    @Test
    fun `time since edit uses the clock`() = runTest(testDispatcher) {
        note(title = "Trip", editedAgo = 30.seconds)
        note(title = "Call", editedAgo = 4.minutes)
        createViewModel().state.test {
            assertThat(awaitItem().recentNotes.map { it.timeSinceEdit })
                .containsExactly(TimeSinceEdit.JustNow, TimeSinceEdit.Minutes(4))
        }
    }

    @Test
    fun `time since edit refreshes every minute without changes to the notes`() = runTest(testDispatcher) {
        note(title = "Trip", editedAgo = 30.seconds)
        createViewModel().state.test {
            assertThat(awaitItem().recentNotes.single().timeSinceEdit).isEqualTo(TimeSinceEdit.JustNow)
            clock.advance(1.minutes)
            advanceTimeBy(1.minutes)
            assertThat(awaitItem().recentNotes.single().timeSinceEdit).isEqualTo(TimeSinceEdit.Minutes(1))
        }
    }

    @Test
    fun `list follows changes to the notes`() = runTest(testDispatcher) {
        val trip = note(title = "Trip")
        createViewModel().state.test {
            assertThat(awaitItem().recentNotes.map { it.title }).containsExactly("Trip")
            dataSource.add(trip.copy(isPinned = true))
            val state = awaitItem()
            assertThat(state.pinnedNotes.map { it.title }).containsExactly("Trip")
            assertThat(state.recentNotes).isEqualTo(emptyList())
        }
    }
}
