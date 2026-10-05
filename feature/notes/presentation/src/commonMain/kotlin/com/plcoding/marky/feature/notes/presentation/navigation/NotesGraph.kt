package com.plcoding.marky.feature.notes.presentation.navigation

import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.plcoding.marky.core.presentation.navigation.LocalNavAnimatedVisibilityScope
import com.plcoding.marky.feature.notes.presentation.list.NotesListRoot
import com.plcoding.marky.feature.notes.presentation.note.NoteRoot

/**
 * @param onSearchClick Opens the search screen of the search feature.
 */
fun NavGraphBuilder.notesGraph(
    navController: NavController,
    onSearchClick: () -> Unit,
) {
    composable<NotesListRoute> {
        CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
            NotesListRoot(
                onNoteClick = { navController.navigate(NoteRoute(noteId = it)) },
                onNewNoteClick = { navController.navigate(NoteRoute()) },
                onSearchClick = onSearchClick,
            )
        }
    }
    composable<NoteRoute> {
        NoteRoot(onBack = { navController.navigateUp() })
    }
}
