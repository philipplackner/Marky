package com.plcoding.marky.core.data.note

/**
 * Turns user input into an FTS4 MATCH expression where every word is a prefix term,
 * all of which have to match.
 *
 * Splits on the same characters the unicode61 tokenizer treats as separators, so FTS
 * syntax characters and operators typed by the user can never reach the query.
 *
 * @return null if the input contains no searchable characters.
 */
internal fun String.toFtsQuery(): String? {
    val tokens = splitWhen { !it.isLetterOrDigit() }
        .filter { it.isNotEmpty() }
    if (tokens.isEmpty()) {
        return null
    }
    // Lowercase so "OR", "AND", "NOT" and "NEAR" are searched as words, not operators.
    return tokens.joinToString(" ") { "${it.lowercase()}*" }
}

private inline fun String.splitWhen(isSeparator: (Char) -> Boolean): List<String> {
    val tokens = mutableListOf<String>()
    val current = StringBuilder()
    for (char in this) {
        if (isSeparator(char)) {
            tokens += current.toString()
            current.clear()
        } else {
            current.append(char)
        }
    }
    tokens += current.toString()
    return tokens
}
