package com.plcoding.marky.feature.search.presentation

sealed interface SearchAction {
    data class OnQueryChange(val query: String) : SearchAction
    data object OnClearClick : SearchAction
}
