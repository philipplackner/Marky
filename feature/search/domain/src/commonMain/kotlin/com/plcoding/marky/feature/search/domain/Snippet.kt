package com.plcoding.marky.feature.search.domain

/**
 * A piece of note text shown under a search result.
 *
 * @property matches ranges in [text] to mark.
 */
data class Snippet(
    val text: String,
    val matches: List<IntRange>,
)

/**
 * Cuts a snippet out of [text].
 *
 * With [startAtFirstMatch], the snippet starts a little before the first of [matches], at a word
 * boundary and with a leading ellipsis. Otherwise it starts at the beginning of the text. Every
 * match inside the snippet stays marked.
 */
fun buildSnippet(
    text: String,
    matches: List<IntRange>,
    startAtFirstMatch: Boolean,
): Snippet {
    val firstMatch = matches.firstOrNull()?.first
    val start = if (startAtFirstMatch && firstMatch != null) snippetStart(text, firstMatch) else 0
    var end = minOf(text.length, start + MAX_SNIPPET_LENGTH)
    if (end < text.length && end > 0 && text[end - 1].isHighSurrogate()) {
        end--
    }
    val prefix = if (start > 0) ELLIPSIS else ""
    val shift = prefix.length - start
    val visibleMatches = matches
        .filter { it.first >= start && it.first < end }
        .map { (it.first + shift)..(minOf(it.last, end - 1) + shift) }
    return Snippet(
        text = prefix + text.substring(start, end),
        matches = visibleMatches,
    )
}

private fun snippetStart(text: String, firstMatch: Int): Int {
    if (firstMatch <= CONTEXT_BEFORE_MATCH) {
        return 0
    }
    val start = firstMatch - CONTEXT_BEFORE_MATCH
    val space = text.indexOf(' ', start)
    if (space in start until firstMatch) {
        return space + 1
    }
    return if (text[start].isLowSurrogate()) start + 1 else start
}

private const val CONTEXT_BEFORE_MATCH = 32
// Enough for two lines on any screen; the result component clamps the rest.
private const val MAX_SNIPPET_LENGTH = 300
private const val ELLIPSIS = "…"
