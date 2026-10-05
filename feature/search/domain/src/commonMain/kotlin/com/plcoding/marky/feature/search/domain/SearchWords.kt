package com.plcoding.marky.feature.search.domain

/**
 * The folded words of a search query. Anything that isn't a letter or digit separates words,
 * the same way the full-text index splits text.
 */
fun searchWords(query: String): List<String> {
    val words = mutableListOf<String>()
    val current = StringBuilder()
    fun flush() {
        val word = current.toString().foldForSearch().text
        if (word.isNotEmpty() && word !in words) {
            words += word
        }
        current.clear()
    }
    for (char in query) {
        if (char.isLetterOrDigit() || char.category == CharCategory.NON_SPACING_MARK) {
            current.append(char)
        } else {
            flush()
        }
    }
    flush()
    return words
}
