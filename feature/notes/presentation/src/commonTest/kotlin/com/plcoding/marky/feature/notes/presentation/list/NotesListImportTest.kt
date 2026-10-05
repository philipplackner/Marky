package com.plcoding.marky.feature.notes.presentation.list

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import assertk.assertions.isTrue
import com.plcoding.marky.core.domain.note.Note
import com.plcoding.marky.core.domain.util.DataError
import com.plcoding.marky.core.presentation.util.UiText
import com.plcoding.marky.feature.notes.presentation.FakeNoteLocalDataSource
import com.plcoding.marky.feature.notes.presentation.TestClock
import com.plcoding.marky.feature.notes.presentation.generated.resources.Res
import com.plcoding.marky.feature.notes.presentation.generated.resources.error_import_disk_full
import com.plcoding.marky.feature.notes.presentation.generated.resources.error_import_save
import com.plcoding.marky.feature.notes.presentation.generated.resources.error_import_unreadable
import com.plcoding.marky.feature.notes.presentation.generated.resources.error_import_wrong_type
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.TimeZone
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class NotesListImportTest {

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

    private fun pick(name: String, text: String) = NotesListAction.OnFilePicked(name, text.encodeToByteArray())

    private fun NotesListEvent.message() = (this as NotesListEvent.ShowSnackbar).message as UiText.Resource

    @Test
    fun `an imported file becomes a new note named after the file`() = runTest(testDispatcher) {
        createViewModel().onAction(pick("ideas.md", "# Ideas\n\nHalf-baked app ideas"))

        val note = dataSource.notes.values.single()
        assertThat(note.title).isEqualTo("ideas")
        assertThat(note.content).isEqualTo("# Ideas\n\nHalf-baked app ideas")
        assertThat(note.isPinned).isEqualTo(false)
        assertThat(note.createdAt).isEqualTo(clock.now)
        assertThat(note.lastEditedAt).isEqualTo(clock.now)
    }

    @Test
    fun `importing a title that exists adds a second note`() = runTest(testDispatcher) {
        val existing = dataSource.add(
            Note(0, "ideas", "Older ideas list", clock.now - 3.hours, clock.now - 3.hours, isPinned = false)
        )
        createViewModel().onAction(pick("ideas.md", "New ideas"))

        assertThat(dataSource.notes.values.map { it.title }).containsExactly("ideas", "ideas")
        assertThat(dataSource.notes.getValue(existing.id)).isEqualTo(existing)
    }

    @Test
    fun `the imported note is highlighted for two seconds`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        viewModel.state.test {
            viewModel.onAction(pick("ideas.md", "text"))
            val importedId = dataSource.notes.keys.single()
            assertThat(expectMostRecentItem().highlightedNoteId).isEqualTo(importedId)

            advanceTimeBy(1_999.milliseconds)
            expectNoEvents()
            advanceTimeBy(1.milliseconds)
            runCurrent()
            assertThat(awaitItem().highlightedNoteId).isNull()
        }
    }

    @Test
    fun `a second import moves the highlight and restarts its timer`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        viewModel.state.test {
            viewModel.onAction(pick("first.md", "1"))
            advanceTimeBy(1.seconds)
            viewModel.onAction(pick("second.md", "2"))
            val secondId = dataSource.notes.values.single { it.title == "second" }.id
            assertThat(expectMostRecentItem().highlightedNoteId).isEqualTo(secondId)

            advanceTimeBy(1.5.seconds)
            runCurrent()
            expectNoEvents()
            advanceTimeBy(1.seconds)
            assertThat(awaitItem().highlightedNoteId).isNull()
        }
    }

    @Test
    fun `a wrong file type offers to choose another file`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        viewModel.events.test {
            viewModel.onAction(NotesListAction.OnWrongFileTypePicked("recipes.pdf"))
            val event = awaitItem() as NotesListEvent.ShowSnackbar
            assertThat(event.offersChooseFile).isTrue()
            assertThat(event.message().id).isEqualTo(Res.string.error_import_wrong_type)
            assertThat(event.message().args.toList()).containsExactly("recipes.pdf")
        }
        assertThat(dataSource.upsertCount).isEqualTo(0)
    }

    @Test
    fun `a file the picker couldn't read shows the unreadable message`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        viewModel.events.test {
            viewModel.onAction(NotesListAction.OnUnreadableFilePicked("ideas.md"))
            val event = awaitItem() as NotesListEvent.ShowSnackbar
            assertThat(event.offersChooseFile).isEqualTo(false)
            assertThat(event.message().id).isEqualTo(Res.string.error_import_unreadable)
        }
    }

    @Test
    fun `a binary file with an md name isn't imported`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        viewModel.events.test {
            viewModel.onAction(NotesListAction.OnFilePicked("ideas.md", byteArrayOf(0xFF.toByte(), 0xFE.toByte(), 0x00)))
            val message = awaitItem().message()
            assertThat(message.id).isEqualTo(Res.string.error_import_unreadable)
            assertThat(message.args.toList()).containsExactly("ideas.md")
        }
        assertThat(dataSource.upsertCount).isEqualTo(0)
    }

    @Test
    fun `a failed save shows an import error per cause`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        viewModel.events.test {
            dataSource.upsertError = DataError.Local.DISK_FULL
            viewModel.onAction(pick("ideas.md", "text"))
            assertThat(awaitItem().message().id).isEqualTo(Res.string.error_import_disk_full)

            dataSource.upsertError = DataError.Local.UNKNOWN
            viewModel.onAction(pick("ideas.md", "text"))
            assertThat(awaitItem().message().id).isEqualTo(Res.string.error_import_save)
        }
        assertThat(dataSource.notes).isEqualTo(emptyMap())
    }
}
