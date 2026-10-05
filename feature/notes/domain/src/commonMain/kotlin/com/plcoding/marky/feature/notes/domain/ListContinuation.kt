package com.plcoding.marky.feature.notes.domain

/**
 * Continues a list when Return is typed at the end of or inside a list, checklist, or
 * quote line: the new line gets the same marker (`- [ ] ` for checklists, the next
 * number for numbered lists). Return on an item with no text removes its marker and
 * ends the list.
 *
 * @return [new] unchanged unless it is [old] with a single line break typed at the cursor.
 */
fun continueListOnNewline(old: EditorText, new: EditorText): EditorText {
    if (old.hasSelection || new.hasSelection) return new
    val cursor = old.selectionStart
    val isSingleNewline = new.text.length == old.text.length + 1 &&
        new.selectionStart == cursor + 1 &&
        new.text[cursor] == '\n' &&
        new.text.startsWith(old.text.substring(0, cursor)) &&
        new.text.endsWith(old.text.substring(cursor))
    if (!isSingleNewline) return new

    val lineStart = lineStartOf(old.text, cursor)
    val lineEnd = lineEndOf(old.text, cursor)
    val line = old.text.substring(lineStart, lineEnd)
    val prefix = parseLinePrefix(line)
    if (prefix.marker == null || prefix.marker == LineMarker.Heading || cursor < lineStart + prefix.length) {
        return new
    }

    if (line.substring(prefix.length).isBlank()) {
        val ended = old.text.substring(0, lineStart) + old.text.substring(lineEnd)
        return EditorText(ended, lineStart)
    }

    val continuation = prefix.indent + when (prefix.marker) {
        LineMarker.Checklist -> prefix.markerText.first() + " [ ] "
        LineMarker.Numbered -> {
            val number = prefix.markerText.takeWhile { it.isDigit() }.toLongOrNull() ?: 0L
            val delimiter = prefix.markerText.dropWhile { it.isDigit() }
            "${number + 1}$delimiter"
        }
        LineMarker.Quote -> "> "
        else -> prefix.markerText
    }
    val insertAt = cursor + 1
    return EditorText(
        text = new.text.substring(0, insertAt) + continuation + new.text.substring(insertAt),
        selectionStart = insertAt + continuation.length,
    )
}
