package com.plcoding.marky.feature.search.presentation.di

import com.plcoding.marky.feature.search.presentation.SearchViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val searchPresentationModule = module {
    viewModelOf(::SearchViewModel)
}
