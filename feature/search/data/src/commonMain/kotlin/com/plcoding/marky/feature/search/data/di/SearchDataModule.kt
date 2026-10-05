package com.plcoding.marky.feature.search.data.di

import com.plcoding.marky.feature.search.data.FullTextNoteSearchDataSource
import com.plcoding.marky.feature.search.domain.NoteSearchDataSource
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module

val searchDataModule = module {
    single<NoteSearchDataSource> {
        FullTextNoteSearchDataSource(
            noteLocalDataSource = get(),
            dispatcher = Dispatchers.Default,
        )
    }
}
