package com.plcoding.marky.feature.notes.presentation.note.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.plcoding.marky.core.designsystem.components.text.MarkyText
import com.plcoding.marky.core.designsystem.markdown.MarkdownEditorStyles
import com.plcoding.marky.core.designsystem.markdown.MarkdownSpacing
import com.plcoding.marky.core.designsystem.markdown.rememberMarkdownEditorStyles
import com.plcoding.marky.core.designsystem.theme.MarkyTheme
import com.plcoding.marky.core.markdown.SyntaxSpan
import com.plcoding.marky.core.markdown.SyntaxStyle
import com.plcoding.marky.core.markdown.markdownSyntaxSpans

/**
 * Write-mode text field for the raw Markdown. Syntax characters fade while headings,
 * bold, italic and code keep their look.
 *
 * @param syntaxSpans Styling for [value]'s text, from `markdownSyntaxSpans`.
 */
@Composable
internal fun MarkdownEditor(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    syntaxSpans: List<SyntaxSpan>,
    placeholder: String,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester = remember { FocusRequester() },
) {
    val styles = rememberMarkdownEditorStyles()
    val transformation = remember(syntaxSpans, styles) { MarkdownSyntaxTransformation(syntaxSpans, styles) }
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        textStyle = styles.text,
        cursorBrush = SolidColor(MarkyTheme.colors.ink),
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
        visualTransformation = transformation,
        decorationBox = { innerTextField ->
            Box(modifier = Modifier.padding(horizontal = MarkdownSpacing.horizontal)) {
                if (value.text.isEmpty()) {
                    MarkyText(
                        text = placeholder,
                        style = styles.text.merge(styles.placeholder),
                    )
                }
                innerTextField()
            }
        },
    )
}

private class MarkdownSyntaxTransformation(
    private val spans: List<SyntaxSpan>,
    private val styles: MarkdownEditorStyles,
) : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val length = text.length
        // Spans are computed in the background and can be one edit behind the text.
        val ranges = spans.mapNotNull { span ->
            val style = styleFor(span.style) ?: return@mapNotNull null
            val start = span.start.coerceIn(0, length)
            val end = span.end.coerceIn(start, length)
            if (start == end) null else AnnotatedString.Range(style, start, end)
        }
        return TransformedText(AnnotatedString(text.text, spanStyles = ranges), OffsetMapping.Identity)
    }

    private fun styleFor(style: SyntaxStyle): SpanStyle? = when (style) {
        SyntaxStyle.Syntax -> styles.syntax
        SyntaxStyle.Heading1 -> styles.heading1
        SyntaxStyle.Heading2 -> styles.heading2
        SyntaxStyle.Heading3 -> styles.heading3
        SyntaxStyle.Bold -> styles.bold
        SyntaxStyle.Italic -> styles.italic
        SyntaxStyle.Code -> styles.code
        SyntaxStyle.Url -> styles.url
        SyntaxStyle.Link -> null
    }

    override fun equals(other: Any?): Boolean =
        other is MarkdownSyntaxTransformation && other.spans == spans && other.styles == styles

    override fun hashCode(): Int = 31 * spans.hashCode() + styles.hashCode()
}

private const val PreviewText = """Flights are **booked**. Still need the *tram pass*.

## Packing
- [x] Passport
- [ ] Rain jacket

> Go to Time Out Market early.

```
TP 1353  Fri 08:40
```

[Tram 28 route](https://carris.pt)"""

@Composable
private fun EditorPreview(value: TextFieldValue, darkTheme: Boolean = false) {
    MarkyTheme(darkTheme = darkTheme) {
        Box(modifier = Modifier.background(MarkyTheme.colors.background).padding(vertical = 16.dp)) {
            MarkdownEditor(
                value = value,
                onValueChange = {},
                syntaxSpans = markdownSyntaxSpans(value.text),
                placeholder = "Start writing…",
            )
        }
    }
}

@Preview(name = "Empty", widthDp = 360)
@Composable
private fun EmptyEditorPreview() {
    EditorPreview(TextFieldValue())
}

@Preview(name = "Text", widthDp = 360)
@Composable
private fun TextEditorPreview() {
    EditorPreview(TextFieldValue(PreviewText))
}

@Preview(name = "Text, dark", widthDp = 360)
@Composable
private fun TextEditorDarkPreview() {
    EditorPreview(TextFieldValue(PreviewText), darkTheme = true)
}

@Preview(name = "Selection", widthDp = 360)
@Composable
private fun SelectionEditorPreview() {
    val start = PreviewText.indexOf("tram pass")
    EditorPreview(TextFieldValue(PreviewText, TextRange(start, start + "tram pass".length)))
}
