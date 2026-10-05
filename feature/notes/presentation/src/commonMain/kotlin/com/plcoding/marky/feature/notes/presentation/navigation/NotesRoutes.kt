package com.plcoding.marky.feature.notes.presentation.navigation

import com.plcoding.marky.feature.notes.presentation.note.NoteViewModel
import kotlinx.serialization.Serializable

@Serializable
data object NotesListRoute

/**
 * @property noteId [NoteViewModel.NEW_NOTE_ID] creates a new note.
 */
@Serializable
data class NoteRoute(val noteId: Long = NoteViewModel.NEW_NOTE_ID)
