package com.plcoding.marky.core.designsystem.markdown

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plcoding.marky.core.designsystem.components.text.MarkBand
import com.plcoding.marky.core.designsystem.components.text.drawTextMarks
import com.plcoding.marky.core.designsystem.theme.MarkyShapes
import com.plcoding.marky.core.designsystem.theme.MarkyTheme
import com.plcoding.marky.core.designsystem.theme.NoLigatures

/**
 * Text with Markdown inline formatting: inline code chips, yellow link underlines
 * and, for H2, the marker swash behind every line.
 */
@Composable
internal fun MarkdownText(
    text: AnnotatedString,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier,
    swash: Boolean = false,
) {
    val colors = MarkyTheme.colors
    var layout by remember { mutableStateOf<TextLayoutResult?>(null) }
    val inlineContent = rememberInlineCode(text)
    val linkRanges = remember(text) {
        text.getLinkAnnotations(0, text.length)
            .filter { it.item is LinkAnnotation.Url }
            .map { it.start until it.end }
    }
    Text(
        text = text,
        modifier = modifier.drawBehind {
            val result = layout ?: return@drawBehind
            if (swash) {
                drawTextMarks(
                    layout = result,
                    ranges = listOf(0 until text.length),
                    color = colors.swash,
                    cornerRadius = 0f,
                    horizontalOutset = 4.dp.toPx(),
                    band = SwashBand,
                )
            }
            if (linkRanges.isNotEmpty()) {
                val offset = 2.dp.toPx()
                val thickness = 4.dp.toPx()
                drawTextMarks(
                    layout = result,
                    ranges = linkRanges,
                    color = colors.highlight,
                    cornerRadius = 0f,
                    horizontalOutset = 0f,
                    band = MarkBand { top, bottom, _ ->
                        val line = result.getLineForVerticalPosition((top + bottom) / 2f)
                        val baseline = result.getLineBaseline(line)
                        (baseline + offset)..(baseline + offset + thickness)
                    },
                )
            }
        },
        style = style,
        color = color,
        inlineContent = inlineContent,
        onTextLayout = { layout = it },
    )
}

// The design paints the H2 marker from 55 % to 92 % of each line box.
private val SwashBand = MarkBand { top, bottom, _ ->
    val height = bottom - top
    (top + height * 0.55f)..(top + height * 0.92f)
}

@Composable
private fun rememberInlineCode(text: AnnotatedString): Map<String, InlineTextContent> {
    val codes = remember(text) {
        text.getStringAnnotations(InlineCodeTag, 0, text.length).map { it.item }.distinct()
    }
    if (codes.isEmpty()) return emptyMap()
    val colors = MarkyTheme.colors
    val codeStyle = MarkyTheme.typography.code.copy(
        fontSize = 14.sp,
        lineHeight = 14.sp,
        fontFeatureSettings = NoLigatures,
        color = colors.ink,
    )
    val measurer = rememberTextMeasurer()
    val density = LocalDensity.current
    return remember(codes, codeStyle, density) {
        codes.associate { code ->
            val textWidth = measurer.measure(code, codeStyle, softWrap = false, maxLines = 1).size.width
            val width = with(density) { (textWidth + 12.dp.toPx()).toSp() }
            "$InlineCodeTag:$code" to InlineTextContent(
                placeholder = Placeholder(
                    width = width,
                    height = 18.sp,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter,
                ),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colors.surface, MarkyShapes.extraSmall),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = code, style = codeStyle, maxLines = 1, softWrap = false)
                }
            }
        }
    }
}
