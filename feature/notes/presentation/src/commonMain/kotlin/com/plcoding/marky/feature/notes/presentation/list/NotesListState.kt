package com.plcoding.marky.feature.notes.presentation.list

import androidx.compose.runtime.Stable

@Stable
data class NotesListState(
    val pinnedNotes: List<NoteUi> = emptyList(),
    val recentNotes: List<NoteUi> = emptyList(),
    val isLoading: Boolean = true,
) {
    val isEmpty: Boolean
        get() = !isLoading && pinnedNotes.isEmpty() && recentNotes.isEmpty()
}
