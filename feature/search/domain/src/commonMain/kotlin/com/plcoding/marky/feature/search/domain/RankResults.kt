package com.plcoding.marky.feature.search.domain

/**
 * Matches [candidates] against [query] and groups them into title and text matches.
 *
 * A candidate is kept only if every word of the query starts a word in its title or text.
 */
fun rankResults(candidates: List<SearchCandidate>, query: String): SearchResults {
    val words = searchWords(query)
    if (words.isEmpty()) {
        return SearchResults.Empty
    }
    val inTitles = mutableListOf<Pair<SearchCandidate, SearchResult>>()
    val inText = mutableListOf<Pair<SearchCandidate, SearchResult>>()
    for (candidate in candidates) {
        if (!containsAllWords(listOf(candidate.title, candidate.plainText), words)) {
            continue
        }
        val titleMatches = findMatches(candidate.title, words)
        val textMatches = findMatches(candidate.plainText, words)
        val isTitleMatch = titleMatches.isNotEmpty()
        val result = SearchResult(
            noteId = candidate.noteId,
            title = candidate.title,
            titleMatches = titleMatches,
            snippet = buildSnippet(
                text = candidate.plainText,
                matches = textMatches,
                startAtFirstMatch = !isTitleMatch,
            ),
        )
        if (isTitleMatch) inTitles += candidate to result else inText += candidate to result
    }
    return SearchResults(
        inTitles = inTitles.sortedByLastEdit(),
        inText = inText.sortedByLastEdit(),
    )
}

private fun List<Pair<SearchCandidate, SearchResult>>.sortedByLastEdit(): List<SearchResult> =
    sortedWith(
        compareByDescending<Pair<SearchCandidate, SearchResult>> { it.first.lastEditedAt }
            .thenByDescending { it.first.noteId }
    ).map { it.second }
