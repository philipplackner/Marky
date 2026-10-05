package com.plcoding.marky.feature.notes.presentation.note

import androidx.compose.ui.text.input.TextFieldValue
import com.plcoding.marky.feature.notes.domain.MarkdownFormat

sealed interface NoteAction {
    data class OnTitleChange(val title: String) : NoteAction
    data class OnBodyChange(val body: TextFieldValue) : NoteAction
    data class OnModeSelect(val mode: NoteMode) : NoteAction
    data object OnStartWritingClick : NoteAction
    data class OnCheckboxToggle(val checkboxOffset: Int) : NoteAction

    /** @param linkTextPlaceholder Link text to insert when nothing is selected. */
    data class OnFormatClick(val format: MarkdownFormat, val linkTextPlaceholder: String) : NoteAction
    data object OnMenuClick : NoteAction
    data object OnMenuDismiss : NoteAction
    data object OnPinClick : NoteAction
    data object OnExportClick : NoteAction
    data object OnDeleteClick : NoteAction
    data object OnDeleteConfirm : NoteAction
    data object OnDeleteDismiss : NoteAction

    /** The app went to the background; save now in case it doesn't come back. */
    data object OnAppBackgrounded : NoteAction
}
