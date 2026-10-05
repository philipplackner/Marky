package com.plcoding.marky.core.designsystem.components.text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.plcoding.marky.core.designsystem.theme.MarkyTheme

/**
 * Text with search matches marked in yellow: rounded 4 dp, 1 dp wider than the
 * match on each side, text in `onHighlight`.
 */
@Composable
internal fun MarkyHighlightedText(
    text: String,
    highlights: List<IntRange>,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
) {
    val colors = MarkyTheme.colors
    var layout by remember { mutableStateOf<TextLayoutResult?>(null) }
    val annotated = remember(text, highlights, colors.onHighlight) {
        buildAnnotatedString {
            append(text)
            highlights.forEach { range ->
                val start = range.first.coerceIn(0, text.length)
                val end = (range.last + 1).coerceIn(start, text.length)
                addStyle(SpanStyle(color = colors.onHighlight), start, end)
            }
        }
    }
    MarkyText(
        text = annotated,
        modifier = modifier.drawBehind {
            layout?.let { result ->
                drawTextMarks(
                    layout = result,
                    ranges = highlights,
                    color = colors.highlight,
                    cornerRadius = 4.dp.toPx(),
                    horizontalOutset = 1.dp.toPx(),
                    band = GlyphBand,
                )
            }
        },
        style = style,
        color = color,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        onTextLayout = { layout = it },
    )
}
