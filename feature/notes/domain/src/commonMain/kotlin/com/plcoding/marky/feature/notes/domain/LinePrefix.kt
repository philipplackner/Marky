package com.plcoding.marky.feature.notes.domain

internal enum class LineMarker {
    Heading,
    Bullet,
    Checklist,
    Numbered,
    Quote,
}

/**
 * The block syntax at the start of a line, for example `  - [ ] ` or `## `.
 *
 * @property length Length of indent plus marker, i.e. where the line's content starts.
 */
internal data class LinePrefix(
    val indent: String,
    val marker: LineMarker?,
    val markerText: String,
) {
    val length: Int
        get() = indent.length + markerText.length

    /** Heading level, or 0 if this isn't a heading. */
    val headingLevel: Int
        get() = if (marker == LineMarker.Heading) markerText.count { it == '#' } else 0
}

internal fun parseLinePrefix(line: String): LinePrefix {
    val indent = line.takeWhile { it == ' ' || it == '\t' }
    val rest = line.substring(indent.length)
    for ((marker, regex) in MarkerPatterns) {
        regex.find(rest)?.let { return LinePrefix(indent, marker, it.value) }
    }
    return LinePrefix(indent, null, "")
}

// Checklist must be tried before bullet, since every checklist marker also starts like a bullet.
private val MarkerPatterns = listOf(
    LineMarker.Heading to Regex("^#{1,6} "),
    LineMarker.Checklist to Regex("^[-*+] \\[[ xX]] "),
    LineMarker.Bullet to Regex("^[-*+] "),
    LineMarker.Numbered to Regex("^\\d{1,9}[.)] "),
    LineMarker.Quote to Regex("^> ?"),
)

internal fun lineStartOf(text: String, position: Int): Int =
    if (position == 0) 0 else text.lastIndexOf('\n', position - 1) + 1

internal fun lineEndOf(text: String, position: Int): Int =
    text.indexOf('\n', position).let { if (it == -1) text.length else it }
