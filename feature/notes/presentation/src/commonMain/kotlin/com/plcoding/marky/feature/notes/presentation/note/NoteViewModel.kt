package com.plcoding.marky.feature.notes.presentation.note

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.marky.core.domain.note.Note
import com.plcoding.marky.core.domain.note.NoteLocalDataSource
import com.plcoding.marky.core.domain.util.DataError
import com.plcoding.marky.core.domain.util.onFailure
import com.plcoding.marky.core.domain.util.onSuccess
import com.plcoding.marky.core.markdown.markdownSyntaxSpans
import com.plcoding.marky.core.markdown.parseMarkdown
import com.plcoding.marky.core.markdown.toggleCheckbox
import com.plcoding.marky.core.presentation.util.UiText
import com.plcoding.marky.feature.notes.domain.MarkdownFormatter
import com.plcoding.marky.feature.notes.domain.continueListOnNewline
import com.plcoding.marky.feature.notes.domain.encodeMarkdownFile
import com.plcoding.marky.feature.notes.domain.exportFileName
import com.plcoding.marky.feature.notes.presentation.generated.resources.Res
import com.plcoding.marky.feature.notes.presentation.generated.resources.error_delete
import com.plcoding.marky.feature.notes.presentation.generated.resources.error_export
import com.plcoding.marky.feature.notes.presentation.generated.resources.error_load
import com.plcoding.marky.feature.notes.presentation.generated.resources.error_pin
import com.plcoding.marky.feature.notes.presentation.generated.resources.error_save
import com.plcoding.marky.feature.notes.presentation.generated.resources.error_save_disk_full
import com.plcoding.marky.feature.notes.presentation.generated.resources.error_unpin
import com.plcoding.marky.feature.notes.presentation.generated.resources.export_done
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/**
 * Edits one note. Changes save automatically shortly after typing stops, when the app
 * goes to the background, and when the screen is left.
 *
 * @param applicationScope Outlives this ViewModel, so the final save on leaving finishes.
 * @param markdownDispatcher Runs Markdown parsing off the main thread.
 */
class NoteViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val noteLocalDataSource: NoteLocalDataSource,
    private val clock: Clock,
    private val applicationScope: CoroutineScope,
    private val markdownDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private var noteId: Long = savedStateHandle[NOTE_ID_KEY] ?: NEW_NOTE_ID
    private val isCreatedHere = noteId == NEW_NOTE_ID

    private val _state = MutableStateFlow(
        if (isCreatedHere) NoteState(mode = NoteMode.Write, isLoading = false, isNewNote = true) else NoteState()
    )
    val state = _state.asStateFlow()

    private val _events = Channel<NoteEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var createdAt: Instant? = null
    private var lastSaved = SavedContent(title = "", body = "")
    private var hasSaveError = false
    private var isDeleted = false
    private val saveMutex = Mutex()
    private var saveJob: Job? = null
    private val bodyTexts = MutableStateFlow<String?>(null)

    init {
        observeSyntax()
        if (!isCreatedHere) {
            loadNote()
        }
    }

    fun onAction(action: NoteAction) {
        if (_state.value.isLoading) return
        when (action) {
            is NoteAction.OnTitleChange -> {
                _state.update { it.copy(title = action.title) }
                scheduleSave()
            }
            is NoteAction.OnBodyChange -> onBodyChange(action.body)
            is NoteAction.OnModeSelect -> selectMode(action.mode)
            NoteAction.OnStartWritingClick -> selectMode(NoteMode.Write)
            is NoteAction.OnCheckboxToggle -> toggleChecklistItem(action.checkboxOffset)
            is NoteAction.OnFormatClick -> {
                val formatted = MarkdownFormatter.apply(
                    format = action.format,
                    value = _state.value.body.toEditorText(),
                    linkTextPlaceholder = action.linkTextPlaceholder,
                )
                setBody(formatted.toTextFieldValue())
            }
            NoteAction.OnMenuClick -> _state.update { it.copy(isMenuOpen = true) }
            NoteAction.OnMenuDismiss -> _state.update { it.copy(isMenuOpen = false) }
            NoteAction.OnPinClick -> togglePin()
            NoteAction.OnExportClick -> exportNote()
            is NoteAction.OnExportSaved -> _events.trySend(
                NoteEvent.ShowSnackbar(UiText.Resource(Res.string.export_done, arrayOf(action.fileName)))
            )
            is NoteAction.OnExportFailed -> _events.trySend(
                NoteEvent.ShowSnackbar(UiText.Resource(Res.string.error_export, arrayOf(action.fileName)))
            )
            NoteAction.OnDeleteClick -> _state.update { it.copy(isMenuOpen = false, isDeleteDialogVisible = true) }
            NoteAction.OnDeleteDismiss -> _state.update { it.copy(isDeleteDialogVisible = false) }
            NoteAction.OnDeleteConfirm -> deleteNote()
            NoteAction.OnAppBackgrounded -> {
                saveJob?.cancel()
                viewModelScope.launch { save() }
            }
        }
    }

    override fun onCleared() {
        saveJob?.cancel()
        applicationScope.launch {
            val content = _state.value.content()
            if (isCreatedHere && content.isBlank()) {
                discardBlankNote()
            } else {
                save()
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeSyntax() {
        bodyTexts
            .filterNotNull()
            .mapLatest { markdownSyntaxSpans(it) }
            .flowOn(markdownDispatcher)
            .onEach { spans ->
                _state.update {
                    it.copy(syntaxSpans = spans, activeFormats = activeFormats(it.body.toEditorText(), spans))
                }
            }
            .launchIn(viewModelScope)
    }

    private fun loadNote() {
        viewModelScope.launch {
            noteLocalDataSource.getNote(noteId)
                .onSuccess { note ->
                    createdAt = note.createdAt
                    lastSaved = SavedContent(note.title, note.content)
                    val document = withContext(markdownDispatcher) { parseMarkdown(note.content) }
                    _state.update {
                        it.copy(
                            title = note.title,
                            body = TextFieldValue(note.content, TextRange(note.content.length)),
                            isPinned = note.isPinned,
                            isLoading = false,
                            mode = NoteMode.Read,
                            document = document,
                        )
                    }
                    bodyTexts.value = note.content
                }
                .onFailure { error ->
                    if (error == DataError.Local.NOT_FOUND) {
                        _events.trySend(NoteEvent.NoteNotFound)
                    } else {
                        _events.trySend(NoteEvent.ShowSnackbar(UiText.Resource(Res.string.error_load)))
                    }
                }
        }
    }

    private fun onBodyChange(newBody: TextFieldValue) {
        val oldBody = _state.value.body
        val continued = continueListOnNewline(oldBody.toEditorText(), newBody.toEditorText())
        setBody(if (continued == newBody.toEditorText()) newBody else continued.toTextFieldValue())
    }

    private fun setBody(body: TextFieldValue) {
        val textChanged = body.text != _state.value.body.text
        _state.update {
            it.copy(body = body, activeFormats = activeFormats(body.toEditorText(), it.syntaxSpans))
        }
        if (textChanged) {
            bodyTexts.value = body.text
            scheduleSave()
        }
    }

    private fun selectMode(mode: NoteMode) {
        _state.update {
            it.copy(
                mode = mode,
                document = if (mode == NoteMode.Read) parseMarkdown(it.body.text) else it.document,
            )
        }
    }

    private fun toggleChecklistItem(checkboxOffset: Int) {
        val body = _state.value.body
        val text = toggleCheckbox(body.text, checkboxOffset)
        if (text == body.text) return
        _state.update {
            it.copy(body = body.copy(text = text), document = parseMarkdown(text))
        }
        bodyTexts.value = text
        scheduleSave()
    }

    private fun togglePin() {
        val isPinned = !_state.value.isPinned
        _state.update { it.copy(isPinned = isPinned, isMenuOpen = false) }
        if (noteId == NEW_NOTE_ID) return
        viewModelScope.launch {
            noteLocalDataSource.setPinned(noteId, isPinned).onFailure {
                _state.update { it.copy(isPinned = !isPinned) }
                val message = if (isPinned) Res.string.error_pin else Res.string.error_unpin
                _events.trySend(NoteEvent.ShowSnackbar(UiText.Resource(message)))
            }
        }
    }

    private fun exportNote() {
        _state.update { it.copy(isMenuOpen = false) }
        saveJob?.cancel()
        viewModelScope.launch {
            save()
            val state = _state.value
            _events.trySend(
                NoteEvent.ExportNote(
                    fileName = exportFileName(state.title),
                    bytes = encodeMarkdownFile(state.body.text),
                )
            )
        }
    }

    private fun deleteNote() {
        saveJob?.cancel()
        _state.update { it.copy(isDeleteDialogVisible = false) }
        viewModelScope.launch {
            saveMutex.withLock {
                if (noteId == NEW_NOTE_ID) {
                    isDeleted = true
                    _events.trySend(NoteEvent.NoteDeleted)
                    return@launch
                }
                noteLocalDataSource.deleteNote(noteId)
                    .onSuccess {
                        isDeleted = true
                        _events.trySend(NoteEvent.NoteDeleted)
                    }
                    .onFailure {
                        _events.trySend(NoteEvent.ShowSnackbar(UiText.Resource(Res.string.error_delete)))
                    }
            }
        }
    }

    private fun scheduleSave() {
        saveJob?.cancel()
        saveJob = viewModelScope.launch {
            delay(AUTOSAVE_DELAY)
            save()
        }
    }

    private suspend fun save() = saveMutex.withLock {
        val state = _state.value
        val content = state.content()
        if (isDeleted || state.isLoading || content == lastSaved) return@withLock
        if (noteId == NEW_NOTE_ID && content.isBlank()) return@withLock

        val now = clock.now()
        val note = Note(
            id = noteId,
            title = content.title,
            content = content.body,
            createdAt = createdAt ?: now,
            lastEditedAt = now,
            isPinned = state.isPinned,
        )
        noteLocalDataSource.upsertNote(note)
            .onSuccess { id ->
                if (noteId == NEW_NOTE_ID) {
                    noteId = id
                    savedStateHandle[NOTE_ID_KEY] = id
                }
                createdAt = note.createdAt
                lastSaved = content
                hasSaveError = false
            }
            .onFailure { error ->
                if (!hasSaveError) {
                    val message = if (error == DataError.Local.DISK_FULL) Res.string.error_save_disk_full else Res.string.error_save
                    _events.trySend(NoteEvent.ShowSnackbar(UiText.Resource(message)))
                }
                hasSaveError = true
            }
    }

    /**
     * A note created here and then emptied again isn't kept, the same as one never written.
     */
    private suspend fun discardBlankNote() = saveMutex.withLock {
        if (noteId != NEW_NOTE_ID && !isDeleted) {
            noteLocalDataSource.deleteNote(noteId)
        }
    }

    private fun NoteState.content() = SavedContent(title = title, body = body.text)

    private data class SavedContent(val title: String, val body: String) {
        fun isBlank() = title.isBlank() && body.isBlank()
    }

    companion object {
        /** Name of the note id argument of [com.plcoding.marky.feature.notes.presentation.navigation.NoteRoute]. */
        const val NOTE_ID_KEY = "noteId"
        const val NEW_NOTE_ID = 0L
        private val AUTOSAVE_DELAY = 500.milliseconds
    }
}
