package com.plcoding.marky.feature.search.presentation.navigation

import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.plcoding.marky.core.presentation.navigation.LocalNavAnimatedVisibilityScope
import com.plcoding.marky.feature.search.presentation.SearchRoot

/**
 * @param onNoteClick Opens a note of the notes feature.
 */
fun NavGraphBuilder.searchGraph(
    onBack: () -> Unit,
    onNoteClick: (noteId: Long) -> Unit,
) {
    composable<SearchRoute> {
        CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
            SearchRoot(onBackClick = onBack, onNoteClick = onNoteClick)
        }
    }
}
