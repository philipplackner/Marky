package com.plcoding.marky.feature.notes.presentation.note

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import assertk.assertions.isTrue
import assertk.assertions.prop
import com.plcoding.marky.core.domain.note.Note
import com.plcoding.marky.core.domain.util.DataError
import com.plcoding.marky.core.markdown.MarkdownBlock
import com.plcoding.marky.core.presentation.util.UiText
import com.plcoding.marky.feature.notes.domain.MarkdownFormat
import com.plcoding.marky.feature.notes.presentation.FakeNoteLocalDataSource
import com.plcoding.marky.feature.notes.presentation.TestClock
import com.plcoding.marky.feature.notes.presentation.generated.resources.Res
import com.plcoding.marky.feature.notes.presentation.generated.resources.error_delete
import com.plcoding.marky.feature.notes.presentation.generated.resources.error_load
import com.plcoding.marky.feature.notes.presentation.generated.resources.error_pin
import com.plcoding.marky.feature.notes.presentation.generated.resources.error_save
import com.plcoding.marky.feature.notes.presentation.generated.resources.error_save_disk_full
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.minutes

class NoteViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val dataSource = FakeNoteLocalDataSource()
    private val clock = TestClock()
    private val store = ViewModelStore()
    private lateinit var savedStateHandle: SavedStateHandle

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(applicationScope: CoroutineScope, noteId: Long = NoteViewModel.NEW_NOTE_ID): NoteViewModel {
        savedStateHandle = SavedStateHandle(mapOf(NoteViewModel.NOTE_ID_KEY to noteId))
        val factory = viewModelFactory {
            initializer {
                NoteViewModel(savedStateHandle, dataSource, clock, applicationScope, testDispatcher)
            }
        }
        return ViewModelProvider.create(store, factory)[NoteViewModel::class]
    }

    private fun existingNote(content: String = "Flights are **booked**.\n\n- [ ] Sunscreen", isPinned: Boolean = false): Note {
        val time = clock.now - 10.minutes
        return dataSource.add(
            Note(id = 0, title = "Trip to Lisbon", content = content, createdAt = time, lastEditedAt = time, isPinned = isPinned),
        )
    }

    private fun NoteViewModel.type(text: String) =
        onAction(NoteAction.OnBodyChange(TextFieldValue(text, TextRange(text.length))))

    private fun TestScope.waitForAutosave() {
        advanceTimeBy(AutosaveDelayMillis + 1)
        runCurrent()
    }

    @Test
    fun `new note opens in write mode and is not saved before anything is typed`() = runTest(testDispatcher) {
        val viewModel = createViewModel(backgroundScope)
        assertThat(viewModel.state.value.mode).isEqualTo(NoteMode.Write)
        assertThat(viewModel.state.value.isNewNote).isTrue()
        waitForAutosave()
        assertThat(dataSource.upsertCount).isEqualTo(0)
    }

    @Test
    fun `typing saves once after the autosave delay`() = runTest(testDispatcher) {
        val viewModel = createViewModel(backgroundScope)
        viewModel.type("M")
        viewModel.type("Mi")
        viewModel.type("Milk")
        advanceTimeBy(AutosaveDelayMillis - 1)
        assertThat(dataSource.upsertCount).isEqualTo(0)
        waitForAutosave()
        assertThat(dataSource.upsertCount).isEqualTo(1)
        assertThat(dataSource.notes.values.single().content).isEqualTo("Milk")
    }

    @Test
    fun `first save inserts and later saves update the same note`() = runTest(testDispatcher) {
        val viewModel = createViewModel(backgroundScope)
        viewModel.onAction(NoteAction.OnTitleChange("Groceries"))
        waitForAutosave()
        val id = dataSource.notes.keys.single()
        assertThat(savedStateHandle.get<Long>(NoteViewModel.NOTE_ID_KEY)).isEqualTo(id)

        clock.advance(1.minutes)
        viewModel.type("Milk")
        waitForAutosave()
        assertThat(dataSource.notes.keys).isEqualTo(setOf(id))
        val note = dataSource.notes.getValue(id)
        assertThat(note.content).isEqualTo("Milk")
        assertThat(note.lastEditedAt).isEqualTo(clock.now)
        assertThat(note.createdAt).isEqualTo(clock.now - 1.minutes)
    }

    @Test
    fun `edit during a slow first insert does not insert a second note`() = runTest(testDispatcher) {
        val gate = CompletableDeferred<Unit>()
        dataSource.upsertGate = gate
        val viewModel = createViewModel(backgroundScope)
        viewModel.type("Milk")
        waitForAutosave()
        viewModel.type("Milk and eggs")
        waitForAutosave()
        gate.complete(Unit)
        runCurrent()
        assertThat(dataSource.notes.values.map { it.content }).containsExactly("Milk and eggs")
    }

    @Test
    fun `whitespace only note is never saved`() = runTest(testDispatcher) {
        val viewModel = createViewModel(backgroundScope)
        viewModel.onAction(NoteAction.OnTitleChange("  "))
        viewModel.type("\n \t")
        waitForAutosave()
        store.clear()
        runCurrent()
        assertThat(dataSource.notes).isEmpty()
    }

    @Test
    fun `leaving the screen saves pending edits right away`() = runTest(testDispatcher) {
        val viewModel = createViewModel(backgroundScope)
        viewModel.type("Milk")
        store.clear()
        runCurrent()
        assertThat(dataSource.notes.values.single().content).isEqualTo("Milk")
    }

    @Test
    fun `note created here and emptied again is deleted on leave`() = runTest(testDispatcher) {
        val viewModel = createViewModel(backgroundScope)
        viewModel.type("Milk")
        waitForAutosave()
        val id = dataSource.notes.keys.single()
        viewModel.type("")
        store.clear()
        runCurrent()
        assertThat(dataSource.deletedIds).containsExactly(id)
        assertThat(dataSource.notes).isEmpty()
    }

    @Test
    fun `emptying an existing note keeps it`() = runTest(testDispatcher) {
        val note = existingNote()
        val viewModel = createViewModel(backgroundScope, note.id)
        viewModel.onAction(NoteAction.OnTitleChange(""))
        viewModel.type("")
        store.clear()
        runCurrent()
        assertThat(dataSource.deletedIds).isEmpty()
        assertThat(dataSource.notes.getValue(note.id).content).isEqualTo("")
    }

    @Test
    fun `opening and leaving a note without edits does not touch it`() = runTest(testDispatcher) {
        val note = existingNote()
        createViewModel(backgroundScope, note.id)
        waitForAutosave()
        store.clear()
        runCurrent()
        assertThat(dataSource.upsertCount).isEqualTo(0)
        assertThat(dataSource.notes.getValue(note.id).lastEditedAt).isEqualTo(note.lastEditedAt)
    }

    @Test
    fun `existing note opens in read mode with its parsed document`() = runTest(testDispatcher) {
        val note = existingNote(isPinned = true)
        val state = createViewModel(backgroundScope, note.id).state.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.isNewNote).isFalse()
        assertThat(state.mode).isEqualTo(NoteMode.Read)
        assertThat(state.title).isEqualTo("Trip to Lisbon")
        assertThat(state.isPinned).isTrue()
        assertThat(state.document.blocks.last()).isInstanceOf<MarkdownBlock.ChecklistItem>()
    }

    @Test
    fun `missing note sends not found`() = runTest(testDispatcher) {
        val viewModel = createViewModel(backgroundScope, noteId = 42)
        viewModel.events.test {
            assertThat(awaitItem()).isEqualTo(NoteEvent.NoteNotFound)
        }
    }

    @Test
    fun `note that fails to load cannot be edited or overwritten`() = runTest(testDispatcher) {
        val note = existingNote()
        dataSource.getError = DataError.Local.UNKNOWN
        val viewModel = createViewModel(backgroundScope, note.id)
        viewModel.events.test {
            assertThat(awaitItem()).isSnackbar(Res.string.error_load)
        }
        viewModel.type("")
        store.clear()
        runCurrent()
        assertThat(dataSource.upsertCount).isEqualTo(0)
        assertThat(dataSource.notes.getValue(note.id).content).isEqualTo(note.content)
    }

    @Test
    fun `ticking a checklist item writes x back and saves`() = runTest(testDispatcher) {
        val note = existingNote()
        val viewModel = createViewModel(backgroundScope, note.id)
        val item = viewModel.state.value.document.blocks.last() as MarkdownBlock.ChecklistItem
        viewModel.onAction(NoteAction.OnCheckboxToggle(item.checkboxOffset))
        val ticked = viewModel.state.value.document.blocks.last() as MarkdownBlock.ChecklistItem
        assertThat(ticked.checked).isTrue()
        waitForAutosave()
        assertThat(dataSource.notes.getValue(note.id).content).isEqualTo("Flights are **booked**.\n\n- [x] Sunscreen")
        assertThat(dataSource.notes.getValue(note.id).lastEditedAt).isEqualTo(clock.now)
    }

    @Test
    fun `checkbox offset that is out of date changes nothing`() = runTest(testDispatcher) {
        val note = existingNote()
        val viewModel = createViewModel(backgroundScope, note.id)
        viewModel.onAction(NoteAction.OnCheckboxToggle(checkboxOffset = 3))
        waitForAutosave()
        assertThat(viewModel.state.value.body.text).isEqualTo(note.content)
        assertThat(dataSource.upsertCount).isEqualTo(0)
    }

    @Test
    fun `switching to read parses the latest text`() = runTest(testDispatcher) {
        val viewModel = createViewModel(backgroundScope)
        viewModel.type("## Packing")
        viewModel.onAction(NoteAction.OnModeSelect(NoteMode.Read))
        assertThat(viewModel.state.value.mode).isEqualTo(NoteMode.Read)
        assertThat(viewModel.state.value.document.blocks.single()).isInstanceOf<MarkdownBlock.Heading>()
    }

    @Test
    fun `return in a checklist continues it`() = runTest(testDispatcher) {
        val viewModel = createViewModel(backgroundScope)
        viewModel.type("- [ ] Milk")
        viewModel.type("- [ ] Milk\n")
        assertThat(viewModel.state.value.body.text).isEqualTo("- [ ] Milk\n- [ ] ")
        assertThat(viewModel.state.value.body.selection).isEqualTo(TextRange(17))
    }

    @Test
    fun `toolbar button formats the selection and marks it active`() = runTest(testDispatcher) {
        val viewModel = createViewModel(backgroundScope)
        viewModel.onAction(NoteAction.OnBodyChange(TextFieldValue("tram pass", TextRange(0, 9))))
        viewModel.onAction(NoteAction.OnFormatClick(MarkdownFormat.Bold, linkTextPlaceholder = "text"))
        val state = viewModel.state.value
        assertThat(state.body.text).isEqualTo("**tram pass**")
        assertThat(state.body.selection).isEqualTo(TextRange(2, 11))
        assertThat(state.activeFormats).isEqualTo(setOf(MarkdownFormat.Bold))
    }

    @Test
    fun `save failure shows one snackbar until a save succeeds again`() = runTest(testDispatcher) {
        dataSource.upsertError = DataError.Local.DISK_FULL
        val viewModel = createViewModel(backgroundScope)
        viewModel.events.test {
            viewModel.type("a")
            waitForAutosave()
            assertThat(awaitItem()).isSnackbar(Res.string.error_save_disk_full)
            viewModel.type("ab")
            waitForAutosave()
            expectNoEvents()

            dataSource.upsertError = null
            viewModel.type("abc")
            waitForAutosave()
            dataSource.upsertError = DataError.Local.UNKNOWN
            viewModel.type("abcd")
            waitForAutosave()
            assertThat(awaitItem()).isSnackbar(Res.string.error_save)
        }
    }

    @Test
    fun `pinning a new note is stored with its first save`() = runTest(testDispatcher) {
        val viewModel = createViewModel(backgroundScope)
        viewModel.onAction(NoteAction.OnPinClick)
        assertThat(viewModel.state.value.isPinned).isTrue()
        viewModel.type("Milk")
        waitForAutosave()
        assertThat(dataSource.notes.values.single().isPinned).isTrue()
    }

    @Test
    fun `pinning keeps the last edit time`() = runTest(testDispatcher) {
        val note = existingNote()
        val viewModel = createViewModel(backgroundScope, note.id)
        viewModel.onAction(NoteAction.OnMenuClick)
        viewModel.onAction(NoteAction.OnPinClick)
        assertThat(viewModel.state.value.isMenuOpen).isFalse()
        assertThat(dataSource.notes.getValue(note.id).isPinned).isTrue()
        assertThat(dataSource.notes.getValue(note.id).lastEditedAt).isEqualTo(note.lastEditedAt)
    }

    @Test
    fun `failed pin is undone with a snackbar`() = runTest(testDispatcher) {
        val note = existingNote()
        dataSource.pinError = DataError.Local.UNKNOWN
        val viewModel = createViewModel(backgroundScope, note.id)
        viewModel.events.test {
            viewModel.onAction(NoteAction.OnPinClick)
            assertThat(awaitItem()).isSnackbar(Res.string.error_pin)
        }
        assertThat(viewModel.state.value.isPinned).isFalse()
    }

    @Test
    fun `confirming delete removes the note and leaves`() = runTest(testDispatcher) {
        val note = existingNote()
        val viewModel = createViewModel(backgroundScope, note.id)
        viewModel.events.test {
            viewModel.onAction(NoteAction.OnDeleteClick)
            assertThat(viewModel.state.value.isDeleteDialogVisible).isTrue()
            viewModel.onAction(NoteAction.OnDeleteConfirm)
            assertThat(awaitItem()).isEqualTo(NoteEvent.NoteDeleted)
        }
        assertThat(viewModel.state.value.isDeleteDialogVisible).isFalse()
        assertThat(dataSource.notes).isEmpty()
    }

    @Test
    fun `pending edit is not saved after the note is deleted`() = runTest(testDispatcher) {
        val note = existingNote()
        val viewModel = createViewModel(backgroundScope, note.id)
        viewModel.type("changed")
        viewModel.onAction(NoteAction.OnDeleteConfirm)
        waitForAutosave()
        store.clear()
        runCurrent()
        assertThat(dataSource.notes).isEmpty()
        assertThat(dataSource.upsertCount).isEqualTo(0)
    }

    @Test
    fun `deleting an unsaved note leaves without touching storage`() = runTest(testDispatcher) {
        val viewModel = createViewModel(backgroundScope)
        viewModel.events.test {
            viewModel.onAction(NoteAction.OnDeleteConfirm)
            assertThat(awaitItem()).isEqualTo(NoteEvent.NoteDeleted)
        }
        assertThat(dataSource.deletedIds).isEmpty()
    }

    @Test
    fun `failed delete keeps the note and shows a snackbar`() = runTest(testDispatcher) {
        val note = existingNote()
        dataSource.deleteError = DataError.Local.UNKNOWN
        val viewModel = createViewModel(backgroundScope, note.id)
        viewModel.events.test {
            viewModel.onAction(NoteAction.OnDeleteConfirm)
            assertThat(awaitItem()).isSnackbar(Res.string.error_delete)
        }
        viewModel.type("still here")
        waitForAutosave()
        assertThat(dataSource.notes.getValue(note.id).content).isEqualTo("still here")
    }

    @Test
    fun `going to the background saves immediately`() = runTest(testDispatcher) {
        val viewModel = createViewModel(backgroundScope)
        viewModel.type("Milk")
        viewModel.onAction(NoteAction.OnAppBackgrounded)
        runCurrent()
        assertThat(dataSource.notes.values.single().content).isEqualTo("Milk")
    }

    @Test
    fun `note saved before process death reopens as an existing note`() = runTest(testDispatcher) {
        val note = existingNote()
        val viewModel = createViewModel(backgroundScope, note.id)
        assertThat(viewModel.state.value.isNewNote).isFalse()
        assertThat(viewModel.state.value.body.text).isEqualTo(note.content)
    }

    private fun assertk.Assert<NoteEvent>.isSnackbar(message: org.jetbrains.compose.resources.StringResource) =
        isInstanceOf<NoteEvent.ShowSnackbar>()
            .prop(NoteEvent.ShowSnackbar::message)
            .isInstanceOf<UiText.Resource>()
            .prop(UiText.Resource::id)
            .isEqualTo(message)

    private companion object {
        const val AutosaveDelayMillis = 500L
    }
}
