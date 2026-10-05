package com.plcoding.marky.feature.notes.presentation.note

import com.plcoding.marky.core.markdown.SyntaxSpan
import com.plcoding.marky.core.markdown.SyntaxStyle
import com.plcoding.marky.feature.notes.domain.EditorText
import com.plcoding.marky.feature.notes.domain.MarkdownFormat
import com.plcoding.marky.feature.notes.domain.MarkdownFormatter

/**
 * The toolbar buttons that turn yellow because the cursor or selection is already
 * inside their format.
 */
internal fun activeFormats(value: EditorText, syntaxSpans: List<SyntaxSpan>): Set<MarkdownFormat> {
    val inline = syntaxSpans
        .filter { it.start < value.selectionStart && value.selectionEnd < it.end }
        .mapNotNull { span ->
            when (span.style) {
                SyntaxStyle.Bold -> MarkdownFormat.Bold
                SyntaxStyle.Italic -> MarkdownFormat.Italic
                SyntaxStyle.Code -> MarkdownFormat.Code
                SyntaxStyle.Link -> MarkdownFormat.Link
                else -> null
            }
        }
    return MarkdownFormatter.activeLineFormats(value) + inline
}
