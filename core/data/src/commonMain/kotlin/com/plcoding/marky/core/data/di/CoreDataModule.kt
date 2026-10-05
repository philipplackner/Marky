package com.plcoding.marky.core.data.di

import com.plcoding.marky.core.data.note.RoomNoteDataSource
import com.plcoding.marky.core.database.MarkyDatabase
import com.plcoding.marky.core.database.createMarkyDatabase
import com.plcoding.marky.core.domain.note.NoteLocalDataSource
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

expect val coreDataPlatformModule: Module

val coreDataModule = module {
    includes(coreDataPlatformModule)

    single { createMarkyDatabase(get()) }
    single { get<MarkyDatabase>().noteDao() }
    singleOf(::RoomNoteDataSource) { bind<NoteLocalDataSource>() }
}
