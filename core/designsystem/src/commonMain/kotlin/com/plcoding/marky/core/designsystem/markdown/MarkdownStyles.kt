package com.plcoding.marky.core.designsystem.markdown

import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plcoding.marky.core.designsystem.theme.MarkyTheme
import com.plcoding.marky.core.designsystem.theme.NoLigatures

/**
 * Vertical spacing between Markdown blocks in Read mode.
 */
object MarkdownSpacing {
    /** Between any two blocks. */
    val block = 12.dp

    /** Above a heading that isn't the first block. */
    val beforeHeading1 = 24.dp
    val beforeHeading2 = 22.dp
    val beforeHeading3 = 18.dp

    /** Between two items of the same list. */
    val listItem = 4.dp

    /** Side padding of the rendered note. */
    val horizontal = 20.dp
}

/**
 * Inline styles for Read mode. Build paragraphs with these plus
 * [appendInlineCode] and [appendLink], then show them with [MarkdownParagraph].
 */
@Immutable
class MarkdownInlineStyles internal constructor(
    val bold: SpanStyle,
    val italic: SpanStyle,
    internal val link: TextLinkStyles,
)

@Composable
fun rememberMarkdownInlineStyles(): MarkdownInlineStyles {
    val colors = MarkyTheme.colors
    return remember(colors) {
        MarkdownInlineStyles(
            bold = SpanStyle(fontWeight = FontWeight.Bold),
            italic = SpanStyle(fontStyle = FontStyle.Italic),
            link = TextLinkStyles(
                style = SpanStyle(color = colors.ink, fontWeight = FontWeight.SemiBold),
                pressedStyle = SpanStyle(background = colors.highlightSoft),
            ),
        )
    }
}

internal const val InlineCodeTag = "marky.inlineCode"

/**
 * Appends `code` as an inline code chip. It never wraps.
 */
fun AnnotatedString.Builder.appendInlineCode(code: String) {
    pushStringAnnotation(tag = InlineCodeTag, annotation = code)
    appendInlineContent(id = "$InlineCodeTag:$code", alternateText = code)
    pop()
}

/**
 * Appends a link that opens [url] in the system browser.
 */
fun AnnotatedString.Builder.appendLink(text: String, url: String, styles: MarkdownInlineStyles) {
    withLink(LinkAnnotation.Url(url = url, styles = styles.link)) {
        append(text)
    }
}

/**
 * Styles for Write mode: the raw Markdown keeps headings, bold and italic visible
 * while syntax characters fade to `inkFaint`.
 *
 * @property text Base style of the editor text (16 / 27).
 */
@Immutable
data class MarkdownEditorStyles(
    val text: TextStyle,
    val syntax: SpanStyle,
    val heading1: SpanStyle,
    val heading2: SpanStyle,
    val heading3: SpanStyle,
    val bold: SpanStyle,
    val italic: SpanStyle,
    val code: SpanStyle,
    val url: SpanStyle,
    val placeholder: SpanStyle,
)

@Composable
fun rememberMarkdownEditorStyles(): MarkdownEditorStyles {
    val colors = MarkyTheme.colors
    val typography = MarkyTheme.typography
    val fonts = MarkyTheme.fonts
    return remember(colors, typography, fonts) {
        MarkdownEditorStyles(
            text = typography.bodyLarge.copy(lineHeight = 27.sp, color = colors.ink),
            syntax = SpanStyle(color = colors.inkFaint),
            heading1 = SpanStyle(fontFamily = fonts.bricolage(22.sp), fontWeight = FontWeight.ExtraBold, fontSize = 22.sp),
            heading2 = SpanStyle(fontFamily = fonts.bricolage(19.sp), fontWeight = FontWeight.ExtraBold, fontSize = 19.sp),
            heading3 = SpanStyle(fontFamily = fonts.bricolage(17.sp), fontWeight = FontWeight.Bold, fontSize = 17.sp),
            bold = SpanStyle(fontWeight = FontWeight.Bold),
            italic = SpanStyle(fontStyle = FontStyle.Italic),
            code = SpanStyle(fontFamily = fonts.jetBrainsMono, fontSize = 14.sp, fontFeatureSettings = NoLigatures),
            url = SpanStyle(color = colors.inkMuted),
            placeholder = SpanStyle(color = colors.inkFaint),
        )
    }
}
