package com.plcoding.marky.feature.notes.presentation.list

import com.plcoding.marky.core.presentation.util.UiText

sealed interface NotesListEvent {
    /**
     * @property offersChooseFile whether the snackbar has a "Choose file" action that opens the picker again.
     */
    data class ShowSnackbar(
        val message: UiText,
        val offersChooseFile: Boolean = false,
    ) : NotesListEvent
}
