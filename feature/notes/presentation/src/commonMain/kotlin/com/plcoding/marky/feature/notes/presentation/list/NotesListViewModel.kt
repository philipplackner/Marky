package com.plcoding.marky.feature.notes.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.marky.core.domain.note.Note
import com.plcoding.marky.core.domain.note.NoteLocalDataSource
import com.plcoding.marky.core.markdown.toPlainPreview
import com.plcoding.marky.feature.notes.domain.timeSinceEdit
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.TimeZone
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

class NotesListViewModel(
    noteLocalDataSource: NoteLocalDataSource,
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

    val state: StateFlow<NotesListState> = combine(
        noteLocalDataSource.observeNotes()
            .map { notes -> notes.map { it.toListEntry() } }
            .flowOn(previewDispatcher),
        now,
    ) { entries, now ->
        val (pinned, recent) = entries.partition { it.isPinned }
        NotesListState(
            pinnedNotes = pinned.map { it.toNoteUi(now) },
            recentNotes = recent.map { it.toNoteUi(now) },
            isLoading = false,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = NotesListState(),
    )

    private fun ListEntry.toNoteUi(now: Instant) = NoteUi(
        id = id,
        title = title,
        preview = preview,
        timeSinceEdit = timeSinceEdit(lastEditedAt, now, timeZone),
    )
}

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
