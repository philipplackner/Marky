package com.plcoding.marky.core.data.di

import com.plcoding.marky.core.database.markyDatabaseBuilder
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val coreDataPlatformModule: Module = module {
    single { markyDatabaseBuilder(androidContext()) }
}
