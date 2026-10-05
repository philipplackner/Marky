package com.plcoding.marky.feature.notes.presentation.note

import com.plcoding.marky.core.presentation.util.UiText

sealed interface NoteEvent {
    data object NoteDeleted : NoteEvent
    data object NoteNotFound : NoteEvent
    data class ShowSnackbar(val message: UiText) : NoteEvent
}
