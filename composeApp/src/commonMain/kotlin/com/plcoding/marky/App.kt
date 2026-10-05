package com.plcoding.marky

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.plcoding.marky.core.designsystem.theme.MarkyTheme
import com.plcoding.marky.core.presentation.navigation.LocalSharedTransitionScope
import com.plcoding.marky.feature.notes.presentation.navigation.NoteRoute
import com.plcoding.marky.feature.notes.presentation.navigation.NotesListRoute
import com.plcoding.marky.feature.notes.presentation.navigation.notesGraph
import com.plcoding.marky.feature.search.presentation.navigation.SearchRoute
import com.plcoding.marky.feature.search.presentation.navigation.searchGraph

@Composable
fun App() {
    MarkyTheme {
        val navController = rememberNavController()
        SharedTransitionLayout {
            CompositionLocalProvider(LocalSharedTransitionScope provides this) {
                NavHost(
                    navController = navController,
                    startDestination = NotesListRoute,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MarkyTheme.colors.background),
                ) {
                    notesGraph(
                        navController = navController,
                        onSearchClick = { navController.navigate(SearchRoute) },
                    )
                    searchGraph(
                        onBack = { navController.navigateUp() },
                        onNoteClick = { navController.navigate(NoteRoute(noteId = it)) },
                    )
                }
            }
        }
    }
}
