package com.plcoding.marky.feature.notes.domain

/**
 * What the formatting toolbar does. Every button types the same Markdown the user
 * could type by hand.
 *
 * - H adds `#` at line start; tapping again gives `##`, `###`, then removes it.
 * - B and I wrap the selection in `**` or `*`, or insert a pair with the cursor inside.
 *   Tapping again on wrapped text removes the markers.
 * - List, checklist and quote add `- `, `- [ ] ` or `> ` at line start; tapping again
 *   removes them. With several lines selected, every non-blank line changes.
 * - Code wraps a selection within one line in backticks, and otherwise wraps the selected
 *   lines or the current line in a ```` ``` ```` block.
 * - Link inserts `[text](https://)` and selects the URL part.
 */
object MarkdownFormatter {

    /**
     * @param linkTextPlaceholder Link text to insert when nothing is selected.
     */
    fun apply(format: MarkdownFormat, value: EditorText, linkTextPlaceholder: String): EditorText = when (format) {
        MarkdownFormat.Heading -> toggleHeading(value)
        MarkdownFormat.Bold -> toggleInline(value, marker = '*', count = 2)
        MarkdownFormat.Italic -> toggleInline(value, marker = '*', count = 1)
        MarkdownFormat.BulletList -> toggleLineMarker(value, LineMarker.Bullet)
        MarkdownFormat.Checklist -> toggleLineMarker(value, LineMarker.Checklist)
        MarkdownFormat.Quote -> toggleLineMarker(value, LineMarker.Quote)
        MarkdownFormat.Code -> toggleCode(value)
        MarkdownFormat.Link -> insertLink(value, linkTextPlaceholder)
    }

    /**
     * The line formats (heading, list, checklist, quote) of the line the cursor is on.
     */
    fun activeLineFormats(value: EditorText): Set<MarkdownFormat> {
        val start = lineStartOf(value.text, value.selectionStart)
        val line = value.text.substring(start, lineEndOf(value.text, value.selectionStart))
        return when (parseLinePrefix(line).marker) {
            LineMarker.Heading -> setOf(MarkdownFormat.Heading)
            LineMarker.Bullet -> setOf(MarkdownFormat.BulletList)
            LineMarker.Checklist -> setOf(MarkdownFormat.Checklist)
            LineMarker.Quote -> setOf(MarkdownFormat.Quote)
            LineMarker.Numbered, null -> emptySet()
        }
    }

    private fun toggleHeading(value: EditorText): EditorText {
        val firstLine = value.text.substring(
            lineStartOf(value.text, value.selectionStart),
            lineEndOf(value.text, value.selectionStart),
        )
        val newLevel = when (val level = parseLinePrefix(firstLine).headingLevel) {
            in 1..2 -> level + 1
            0 -> 1
            else -> 0
        }
        val newMarker = if (newLevel == 0) "" else "#".repeat(newLevel) + " "
        return rewriteLinePrefixes(value, skipBlankLines = false) { prefix ->
            if (prefix.marker == LineMarker.Heading || prefix.marker == null) {
                prefix.copy(marker = LineMarker.Heading, markerText = newMarker)
            } else {
                prefix.copy(markerText = newMarker + prefix.markerText)
            }
        }
    }

    private fun toggleLineMarker(value: EditorText, target: LineMarker): EditorText {
        val lines = selectedLines(value)
        val contentLines = lines.filter { it.isNotBlank() }.ifEmpty { lines }
        val removing = contentLines.all { parseLinePrefix(it).marker == target }
        return rewriteLinePrefixes(value, skipBlankLines = lines.size > 1) { prefix ->
            when {
                removing -> prefix.copy(marker = null, markerText = "")
                prefix.marker == target -> prefix
                target == LineMarker.Quote -> prefix.copy(
                    indent = "",
                    marker = LineMarker.Quote,
                    markerText = "> " + prefix.indent + prefix.markerText,
                )
                prefix.marker in ListMarkers || prefix.marker == null -> prefix.copy(
                    marker = target,
                    markerText = markerTextFor(target),
                )
                else -> prefix.copy(markerText = prefix.markerText + markerTextFor(target))
            }
        }
    }

    private fun markerTextFor(marker: LineMarker): String = when (marker) {
        LineMarker.Bullet -> "- "
        LineMarker.Checklist -> "- [ ] "
        LineMarker.Quote -> "> "
        LineMarker.Numbered -> "1. "
        LineMarker.Heading -> "# "
    }

    private fun selectedLines(value: EditorText): List<String> {
        val start = lineStartOf(value.text, value.selectionStart)
        val end = lineEndOf(value.text, value.selectionEnd)
        return value.text.substring(start, end).split('\n')
    }

    /**
     * Replaces the prefix of every line touched by the selection and moves the selection
     * so it stays on the same content.
     */
    private fun rewriteLinePrefixes(
        value: EditorText,
        skipBlankLines: Boolean,
        transform: (LinePrefix) -> LinePrefix,
    ): EditorText {
        val text = value.text
        val blockStart = lineStartOf(text, value.selectionStart)
        val blockEnd = lineEndOf(text, value.selectionEnd)
        val result = StringBuilder(text.substring(0, blockStart))
        var newStart = value.selectionStart
        var newEnd = value.selectionEnd
        var oldLineStart = blockStart
        for (line in text.substring(blockStart, blockEnd).split('\n')) {
            if (oldLineStart > blockStart) result.append('\n')
            val newLineStart = result.length
            val oldPrefix = parseLinePrefix(line)
            val newPrefix = if (skipBlankLines && line.isBlank()) oldPrefix else transform(oldPrefix)
            result.append(newPrefix.indent).append(newPrefix.markerText).append(line.substring(oldPrefix.length))

            fun map(position: Int): Int {
                val offsetInContent = (position - oldLineStart - oldPrefix.length).coerceAtLeast(0)
                return newLineStart + newPrefix.length + offsetInContent
            }
            val oldLineEnd = oldLineStart + line.length
            if (value.selectionStart in oldLineStart..oldLineEnd) newStart = map(value.selectionStart)
            if (value.selectionEnd in oldLineStart..oldLineEnd) newEnd = map(value.selectionEnd)
            oldLineStart = oldLineEnd + 1
        }
        result.append(text.substring(blockEnd))
        return EditorText(result.toString(), newStart, newEnd)
    }

    private fun toggleInline(value: EditorText, marker: Char, count: Int): EditorText {
        val text = value.text
        if (!value.hasSelection) {
            val cursor = value.selectionStart
            val before = runBefore(text, cursor, marker)
            val after = runAfter(text, cursor, marker)
            if (before == count && after == count) {
                return EditorText(text.removeRange(cursor - count, cursor + count), cursor - count)
            }
            val pair = marker.toString().repeat(count * 2)
            return EditorText(text.substring(0, cursor) + pair + text.substring(cursor), cursor + count)
        }

        val start = value.selectionStart + text.substring(value.selectionStart, value.selectionEnd).takeWhile { it.isWhitespace() }.length
        val end = value.selectionEnd - text.substring(start, value.selectionEnd).takeLastWhile { it.isWhitespace() }.length
        if (start >= end) {
            return toggleInline(EditorText(text, value.selectionEnd), marker, count)
        }

        if (hasMarkers(runBefore(text, start, marker), runAfter(text, end, marker), count)) {
            val unwrapped = text.substring(0, start - count) + text.substring(start, end) + text.substring(end + count)
            return EditorText(unwrapped, start - count, end - count)
        }
        val selected = text.substring(start, end)
        val innerBefore = selected.takeWhile { it == marker }.length
        val innerAfter = selected.takeLastWhile { it == marker }.length
        if (selected.length >= count * 2 && hasMarkers(innerBefore, innerAfter, count)) {
            val unwrapped = text.substring(0, start) + selected.substring(count, selected.length - count) + text.substring(end)
            return EditorText(unwrapped, start, end - count * 2)
        }
        val markers = marker.toString().repeat(count)
        val wrapped = text.substring(0, start) + markers + selected + markers + text.substring(end)
        return EditorText(wrapped, start + count, end + count)
    }

    /**
     * Whether runs of [before] and [after] marker characters around some text contain a
     * marker of length [count]. `***` holds both bold and italic.
     */
    private fun hasMarkers(before: Int, after: Int, count: Int): Boolean {
        val shared = minOf(before, after)
        return when (count) {
            1 -> shared == 1 || shared >= 3
            else -> shared >= count
        }
    }

    private fun runBefore(text: String, position: Int, char: Char): Int =
        text.substring(0, position).takeLastWhile { it == char }.length

    private fun runAfter(text: String, position: Int, char: Char): Int =
        text.substring(position).takeWhile { it == char }.length

    private fun toggleCode(value: EditorText): EditorText {
        val text = value.text
        val selected = text.substring(value.selectionStart, value.selectionEnd)
        if (value.hasSelection && '\n' !in selected) {
            return toggleInline(value, marker = '`', count = 1)
        }
        val blockStart = lineStartOf(text, value.selectionStart)
        val blockEnd = lineEndOf(text, value.selectionEnd)

        val previousLineStart = if (blockStart > 0) lineStartOf(text, blockStart - 1) else -1
        val nextLineEnd = if (blockEnd < text.length) lineEndOf(text, blockEnd + 1) else -1
        val isFenced = previousLineStart >= 0 && nextLineEnd >= 0 &&
            text.substring(previousLineStart, blockStart - 1).trim().startsWith(Fence) &&
            text.substring(blockEnd + 1, nextLineEnd).trim() == Fence
        if (isFenced) {
            val openingLength = blockStart - previousLineStart
            val unfenced = text.substring(0, previousLineStart) + text.substring(blockStart, blockEnd) + text.substring(nextLineEnd)
            return EditorText(unfenced, value.selectionStart - openingLength, value.selectionEnd - openingLength)
        }

        val opening = "$Fence\n"
        val fenced = text.substring(0, blockStart) + opening + text.substring(blockStart, blockEnd) + "\n$Fence" + text.substring(blockEnd)
        return EditorText(fenced, value.selectionStart + opening.length, value.selectionEnd + opening.length)
    }

    private fun insertLink(value: EditorText, placeholder: String): EditorText {
        val text = value.text
        val label = if (value.hasSelection) text.substring(value.selectionStart, value.selectionEnd) else placeholder
        val link = "[$label]($UrlPlaceholder)"
        val urlStart = value.selectionStart + label.length + 3
        return EditorText(
            text = text.substring(0, value.selectionStart) + link + text.substring(value.selectionEnd),
            selectionStart = urlStart,
            selectionEnd = urlStart + UrlPlaceholder.length,
        )
    }

    private val ListMarkers = setOf(LineMarker.Bullet, LineMarker.Checklist, LineMarker.Numbered)
    private const val Fence = "```"
    private const val UrlPlaceholder = "https://"
}
