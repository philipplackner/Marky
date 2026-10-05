package com.plcoding.marky

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.plcoding.marky.core.designsystem.theme.MarkyTheme
import com.plcoding.marky.feature.notes.presentation.navigation.NotesListRoute
import com.plcoding.marky.feature.notes.presentation.navigation.notesGraph

@Composable
fun App() {
    MarkyTheme {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = NotesListRoute,
            modifier = Modifier
                .fillMaxSize()
                .background(MarkyTheme.colors.background),
        ) {
            // Search and import come in their own batches.
            notesGraph(
                navController = navController,
                onSearchClick = {},
                onImportClick = {},
            )
        }
    }
}
