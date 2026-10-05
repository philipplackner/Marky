package com.plcoding.marky.di

import com.plcoding.marky.core.data.di.coreDataModule
import com.plcoding.marky.feature.notes.presentation.di.notesPresentationModule
import com.plcoding.marky.feature.search.data.di.searchDataModule
import com.plcoding.marky.feature.search.presentation.di.searchPresentationModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(
            appModule,
            coreDataModule,
            notesPresentationModule,
            searchDataModule,
            searchPresentationModule,
        )
    }
}
