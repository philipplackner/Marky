package com.plcoding.marky.feature.search.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.marky.core.domain.util.Result
import com.plcoding.marky.feature.search.domain.NoteSearchDataSource
import com.plcoding.marky.feature.search.domain.SearchResults
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class SearchViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val noteSearchDataSource: NoteSearchDataSource,
) : ViewModel() {

    private val _state = MutableStateFlow(SearchState(query = savedStateHandle[QUERY_KEY] ?: ""))
    val state = _state.asStateFlow()

    init {
        observeResults()
    }

    fun onAction(action: SearchAction) {
        when (action) {
            is SearchAction.OnQueryChange -> changeQuery(action.query)
            SearchAction.OnClearClick -> changeQuery("")
        }
    }

    private fun changeQuery(query: String) {
        savedStateHandle[QUERY_KEY] = query
        _state.update { it.copy(query = query) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeResults() {
        _state
            .map { it.query }
            .distinctUntilChanged()
            .flatMapLatest { query ->
                if (query.isBlank()) {
                    flowOf(query to SearchResults.Empty)
                } else {
                    noteSearchDataSource.search(query).map { result ->
                        // No design for a failed search; it reads the same as no matches.
                        query to ((result as? Result.Success)?.data ?: SearchResults.Empty)
                    }
                }
            }
            .onEach { (query, results) ->
                _state.update {
                    it.copy(
                        searchedQuery = query,
                        inTitles = results.inTitles.map { result -> result.toSearchResultUi() },
                        inText = results.inText.map { result -> result.toSearchResultUi() },
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    companion object {
        const val QUERY_KEY = "query"
    }
}
