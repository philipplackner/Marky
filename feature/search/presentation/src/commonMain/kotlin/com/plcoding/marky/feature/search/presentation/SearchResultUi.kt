package com.plcoding.marky.feature.search.presentation

import com.plcoding.marky.feature.search.domain.SearchResult

/**
 * @property title null for a note without a title.
 * @property snippet null for a note without text.
 */
data class SearchResultUi(
    val noteId: Long,
    val title: String?,
    val titleMatches: List<IntRange>,
    val snippet: String?,
    val snippetMatches: List<IntRange>,
)

fun SearchResult.toSearchResultUi() = SearchResultUi(
    noteId = noteId,
    title = title.ifBlank { null },
    titleMatches = titleMatches,
    snippet = snippet.text.ifEmpty { null },
    snippetMatches = snippet.matches,
)
