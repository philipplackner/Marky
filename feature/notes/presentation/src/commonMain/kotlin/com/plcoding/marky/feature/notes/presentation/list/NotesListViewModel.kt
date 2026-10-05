package com.plcoding.marky.feature.notes.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.marky.core.domain.note.Note
import com.plcoding.marky.core.domain.note.NoteLocalDataSource
import com.plcoding.marky.core.domain.util.DataError
import com.plcoding.marky.core.domain.util.onFailure
import com.plcoding.marky.core.domain.util.onSuccess
import com.plcoding.marky.core.markdown.toPlainPreview
import com.plcoding.marky.core.presentation.util.UiText
import com.plcoding.marky.feature.notes.domain.decodeMarkdownFile
import com.plcoding.marky.feature.notes.domain.noteTitleFromFileName
import com.plcoding.marky.feature.notes.domain.timeSinceEdit
import com.plcoding.marky.feature.notes.presentation.generated.resources.Res
import com.plcoding.marky.feature.notes.presentation.generated.resources.error_import_disk_full
import com.plcoding.marky.feature.notes.presentation.generated.resources.error_import_save
import com.plcoding.marky.feature.notes.presentation.generated.resources.error_import_unreadable
import com.plcoding.marky.feature.notes.presentation.generated.resources.error_import_wrong_type
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone

class NotesListViewModel(
    private val noteLocalDataSource: NoteLocalDataSource,
    private val clock: Clock,
    private val timeZone: TimeZone,
    previewDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val now = flow {
        while (true) {
            emit(clock.now())
            delay(1.minutes)
        }
    }

    private val highlightedNoteId = MutableStateFlow<Long?>(null)
    private var highlightJob: Job? = null

    private val _events = Channel<NotesListEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    val state: StateFlow<NotesListState> = combine(
        noteLocalDataSource.observeNotes()
            .map { notes -> notes.map { it.toListEntry() } }
            .flowOn(previewDispatcher),
        now,
        highlightedNoteId,
    ) { entries, now, highlightedNoteId ->
        val (pinned, recent) = entries.partition { it.isPinned }
        NotesListState(
            pinnedNotes = pinned.map { it.toNoteUi(now) },
            recentNotes = recent.map { it.toNoteUi(now) },
            isLoading = false,
            highlightedNoteId = highlightedNoteId,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = NotesListState(),
    )

    fun onAction(action: NotesListAction) {
        when (action) {
            is NotesListAction.OnFilePicked -> importFile(action.fileName, action.bytes)
            is NotesListAction.OnWrongFileTypePicked -> _events.trySend(
                NotesListEvent.ShowSnackbar(
                    message = UiText.Resource(Res.string.error_import_wrong_type, arrayOf(action.fileName)),
                    offersChooseFile = true,
                )
            )
            is NotesListAction.OnUnreadableFilePicked -> sendUnreadable(action.fileName)
        }
    }

    private fun importFile(fileName: String, bytes: ByteArray) {
        val text = decodeMarkdownFile(bytes) ?: return sendUnreadable(fileName)
        val now = clock.now()
        val note = Note(
            id = 0,
            title = noteTitleFromFileName(fileName),
            content = text,
            createdAt = now,
            lastEditedAt = now,
            isPinned = false,
        )
        viewModelScope.launch {
            noteLocalDataSource.upsertNote(note)
                .onSuccess { id -> highlight(id) }
                .onFailure { error ->
                    val message = if (error == DataError.Local.DISK_FULL) {
                        Res.string.error_import_disk_full
                    } else {
                        Res.string.error_import_save
                    }
                    _events.trySend(NotesListEvent.ShowSnackbar(UiText.Resource(message, arrayOf(fileName))))
                }
        }
    }

    private fun sendUnreadable(fileName: String) {
        _events.trySend(
            NotesListEvent.ShowSnackbar(UiText.Resource(Res.string.error_import_unreadable, arrayOf(fileName)))
        )
    }

    private fun highlight(noteId: Long) {
        highlightJob?.cancel()
        highlightedNoteId.value = noteId
        highlightJob = viewModelScope.launch {
            delay(HIGHLIGHT_DURATION)
            highlightedNoteId.value = null
        }
    }

    private fun ListEntry.toNoteUi(now: Instant) = NoteUi(
        id = id,
        title = title,
        preview = preview,
        timeSinceEdit = timeSinceEdit(lastEditedAt, now, timeZone),
    )
}

private val HIGHLIGHT_DURATION = 2.seconds

/**
 * A note reduced to what the list shows, so previews are only rebuilt when notes change,
 * not every minute.
 */
private data class ListEntry(
    val id: Long,
    val title: String?,
    val preview: String?,
    val lastEditedAt: Instant,
    val isPinned: Boolean,
)

private fun Note.toListEntry() = ListEntry(
    id = id,
    title = title.trim().ifEmpty { null },
    preview = content.toPlainPreview().ifEmpty { null },
    lastEditedAt = lastEditedAt,
    isPinned = isPinned,
)
