package com.plcoding.marky.feature.notes.presentation.di

import com.plcoding.marky.feature.notes.presentation.list.NotesListViewModel
import com.plcoding.marky.feature.notes.presentation.note.NoteViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.TimeZone
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import kotlin.time.Clock

val notesPresentationModule = module {
    viewModel {
        NotesListViewModel(
            noteLocalDataSource = get(),
            clock = Clock.System,
            timeZone = TimeZone.currentSystemDefault(),
            previewDispatcher = Dispatchers.Default,
        )
    }
    viewModel {
        NoteViewModel(
            savedStateHandle = get(),
            noteLocalDataSource = get(),
            clock = Clock.System,
            applicationScope = get(),
            markdownDispatcher = Dispatchers.Default,
        )
    }
}
