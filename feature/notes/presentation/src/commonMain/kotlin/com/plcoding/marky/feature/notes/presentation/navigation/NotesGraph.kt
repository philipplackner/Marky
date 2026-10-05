package com.plcoding.marky.feature.notes.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.plcoding.marky.feature.notes.presentation.list.NotesListRoot
import com.plcoding.marky.feature.notes.presentation.note.NoteRoot

/**
 * @param onSearchClick Opens the search screen of the search feature.
 * @param onImportClick Starts importing a `.md` file.
 */
fun NavGraphBuilder.notesGraph(
    navController: NavController,
    onSearchClick: () -> Unit,
    onImportClick: () -> Unit,
) {
    composable<NotesListRoute> {
        NotesListRoot(
            onNoteClick = { navController.navigate(NoteRoute(noteId = it)) },
            onNewNoteClick = { navController.navigate(NoteRoute()) },
            onSearchClick = onSearchClick,
            onImportClick = onImportClick,
        )
    }
    composable<NoteRoute> {
        NoteRoot(onBack = { navController.navigateUp() })
    }
}
