package com.plcoding.marky.feature.search.domain

/**
 * Splits [char] into its base letter and combining marks (canonical decomposition).
 */
internal expect fun decompose(char: Char): String

/**
 * Text folded for matching, so case and accents are ignored.
 *
 * @property text the folded text.
 * @property originalIndices for every index in [text], the index of the character in the
 * original text it came from.
 */
internal class FoldedText(
    val text: String,
    private val originalIndices: IntArray,
    private val originalLength: Int,
) {
    /**
     * Maps the folded range `[start, endExclusive)` back to the original text. Characters that
     * fold to nothing, like combining marks after the last matched letter, are included.
     */
    fun originalRange(start: Int, endExclusive: Int): IntRange {
        val originalEnd = if (endExclusive < text.length) originalIndices[endExclusive] else originalLength
        return originalIndices[start] until originalEnd
    }
}

internal fun String.foldForSearch(): FoldedText {
    val folded = StringBuilder(length)
    val indices = IntArray(length * 2)
    var count = 0
    var indexMap = indices
    forEachIndexed { index, char ->
        val piece = foldChar(char)
        for (foldedChar in piece) {
            if (count == indexMap.size) {
                indexMap = indexMap.copyOf(indexMap.size * 2 + 1)
            }
            folded.append(foldedChar)
            indexMap[count++] = index
        }
    }
    return FoldedText(folded.toString(), indexMap.copyOf(count), length)
}

private fun foldChar(char: Char): String {
    if (char.code < 0x80) {
        return char.lowercaseChar().toString()
    }
    if (char.isSurrogate()) {
        return char.toString()
    }
    return decompose(char)
        .lowercase()
        .filterNot { it.category == CharCategory.NON_SPACING_MARK }
}
