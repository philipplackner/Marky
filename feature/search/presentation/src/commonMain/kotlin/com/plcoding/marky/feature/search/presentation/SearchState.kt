package com.plcoding.marky.feature.search.presentation

/**
 * @property searchedQuery the query [inTitles] and [inText] belong to. While it lags behind
 * [query], the previous results stay visible instead of flashing "no results".
 */
data class SearchState(
    val query: String = "",
    val searchedQuery: String = "",
    val inTitles: List<SearchResultUi> = emptyList(),
    val inText: List<SearchResultUi> = emptyList(),
) {
    val status: SearchStatus
        get() = when {
            query.isBlank() -> SearchStatus.Start
            inTitles.isEmpty() && inText.isEmpty() && searchedQuery == query -> SearchStatus.NoResults
            else -> SearchStatus.Results
        }
}

enum class SearchStatus {
    Start,
    Results,
    NoResults,
}
