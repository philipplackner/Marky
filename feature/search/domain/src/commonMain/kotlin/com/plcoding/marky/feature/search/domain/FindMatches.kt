package com.plcoding.marky.feature.search.domain

/**
 * Every place in [text] where a word starts with one of [words], ignoring case and accents.
 *
 * @param words folded words from [searchWords].
 * @return ranges in [text], sorted and with overlaps merged.
 */
fun findMatches(text: String, words: List<String>): List<IntRange> {
    if (text.isEmpty() || words.isEmpty()) {
        return emptyList()
    }
    val folded = text.foldForSearch()
    val ranges = words.flatMap { word ->
        buildList {
            var start = folded.text.indexOf(word)
            while (start >= 0) {
                if (start == 0 || !folded.text[start - 1].isLetterOrDigit()) {
                    add(folded.originalRange(start, start + word.length))
                }
                start = folded.text.indexOf(word, start + 1)
            }
        }
    }
    return ranges.mergeOverlapping()
}

/**
 * Whether every one of [words] starts a word somewhere in [texts].
 */
internal fun containsAllWords(texts: List<String>, words: List<String>): Boolean {
    val folded = texts.map { it.foldForSearch().text }
    return words.all { word -> folded.any { it.containsWordPrefix(word) } }
}

private fun String.containsWordPrefix(word: String): Boolean {
    var start = indexOf(word)
    while (start >= 0) {
        if (start == 0 || !this[start - 1].isLetterOrDigit()) {
            return true
        }
        start = indexOf(word, start + 1)
    }
    return false
}

private fun List<IntRange>.mergeOverlapping(): List<IntRange> {
    val merged = mutableListOf<IntRange>()
    for (range in sortedBy { it.first }) {
        val last = merged.lastOrNull()
        if (last != null && range.first <= last.last + 1) {
            merged[merged.lastIndex] = last.first..maxOf(last.last, range.last)
        } else {
            merged += range
        }
    }
    return merged
}
