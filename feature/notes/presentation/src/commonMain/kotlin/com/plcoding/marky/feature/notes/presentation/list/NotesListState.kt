package com.plcoding.marky.feature.notes.presentation.list

import androidx.compose.runtime.Stable

/**
 * @property highlightedNoteId a just-imported note, shown soft yellow for a moment.
 */
@Stable
data class NotesListState(
    val pinnedNotes: List<NoteUi> = emptyList(),
    val recentNotes: List<NoteUi> = emptyList(),
    val isLoading: Boolean = true,
    val highlightedNoteId: Long? = null,
) {
    val isEmpty: Boolean
        get() = !isLoading && pinnedNotes.isEmpty() && recentNotes.isEmpty()
}
