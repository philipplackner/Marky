package com.plcoding.marky.feature.notes.presentation.list

sealed interface NotesListAction {
    class OnFilePicked(val fileName: String, val bytes: ByteArray) : NotesListAction
    data class OnWrongFileTypePicked(val fileName: String) : NotesListAction
    data class OnUnreadableFilePicked(val fileName: String) : NotesListAction
}
