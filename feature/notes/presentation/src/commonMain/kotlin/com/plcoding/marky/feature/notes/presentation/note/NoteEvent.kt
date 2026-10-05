package com.plcoding.marky.feature.notes.presentation.note

import com.plcoding.marky.core.presentation.util.UiText

sealed interface NoteEvent {
    data object NoteDeleted : NoteEvent
    data object NoteNotFound : NoteEvent
    data class ShowSnackbar(val message: UiText) : NoteEvent

    /**
     * Opens the system save dialog for the note as a `.md` file.
     *
     * @property fileName null when the note has no title, so the UI names it "Untitled.md".
     */
    class ExportNote(val fileName: String?, val bytes: ByteArray) : NoteEvent
}
