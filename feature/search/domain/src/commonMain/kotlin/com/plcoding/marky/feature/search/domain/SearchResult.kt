package com.plcoding.marky.feature.search.domain

import kotlin.time.Instant

/**
 * A note that might match a search, with its text already stripped of Markdown syntax.
 */
data class SearchCandidate(
    val noteId: Long,
    val title: String,
    val plainText: String,
    val lastEditedAt: Instant,
)

/**
 * @property titleMatches ranges in [title] to mark.
 */
data class SearchResult(
    val noteId: Long,
    val title: String,
    val titleMatches: List<IntRange>,
    val snippet: Snippet,
)

/**
 * Search results grouped like on the search screen: notes with a matching title first, then
 * notes that only match in their text. Each group is sorted by last edit, newest first.
 */
data class SearchResults(
    val inTitles: List<SearchResult>,
    val inText: List<SearchResult>,
) {
    val isEmpty: Boolean
        get() = inTitles.isEmpty() && inText.isEmpty()

    companion object {
        val Empty = SearchResults(emptyList(), emptyList())
    }
}
