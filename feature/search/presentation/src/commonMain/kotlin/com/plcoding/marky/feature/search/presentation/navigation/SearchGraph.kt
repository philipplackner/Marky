package com.plcoding.marky.feature.search.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.plcoding.marky.feature.search.presentation.SearchRoot

/**
 * @param onNoteClick Opens a note of the notes feature.
 */
fun NavGraphBuilder.searchGraph(
    onBack: () -> Unit,
    onNoteClick: (noteId: Long) -> Unit,
) {
    composable<SearchRoute> {
        SearchRoot(onBackClick = onBack, onNoteClick = onNoteClick)
    }
}
