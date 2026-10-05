package com.plcoding.marky.core.designsystem.components.text

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextLayoutResult

/**
 * Vertical extent of a mark inside one line, from the line's top and bottom in px.
 */
internal fun interface MarkBand {
    fun span(lineTop: Float, lineBottom: Float, fontSizePx: Float): ClosedFloatingPointRange<Float>
}

/** Behind the glyphs only, like a CSS inline background. */
internal val GlyphBand = MarkBand { top, bottom, fontSize ->
    val center = (top + bottom) / 2f
    val half = fontSize * 0.625f
    (center - half)..(center + half)
}

/**
 * Draws a rounded rectangle behind every [ranges] part of the laid-out text, split per line.
 * Parts cut off by `maxLines` are skipped.
 */
internal fun DrawScope.drawTextMarks(
    layout: TextLayoutResult,
    ranges: List<IntRange>,
    color: Color,
    cornerRadius: Float,
    horizontalOutset: Float,
    band: MarkBand,
) {
    val fontSizePx = layout.layoutInput.style.fontSize.toPx()
    val visibleEnd = layout.getLineEnd(layout.lineCount - 1, visibleEnd = true)
    ranges.forEach { range ->
        val start = range.first.coerceAtLeast(0)
        val end = (range.last + 1).coerceAtMost(visibleEnd)
        if (start >= end) return@forEach
        val firstLine = layout.getLineForOffset(start)
        val lastLine = layout.getLineForOffset(end - 1)
        for (line in firstLine..lastLine) {
            val lineStart = maxOf(start, layout.getLineStart(line))
            val lineEnd = minOf(end, layout.getLineEnd(line, visibleEnd = true))
            if (lineStart >= lineEnd) continue
            val x1 = layout.getHorizontalPosition(lineStart, usePrimaryDirection = true)
            val x2 = layout.getHorizontalPosition(lineEnd, usePrimaryDirection = true)
            val left = minOf(x1, x2) - horizontalOutset
            val right = maxOf(x1, x2) + horizontalOutset
            val span = band.span(layout.getLineTop(line), layout.getLineBottom(line), fontSizePx)
            drawRoundRect(
                color = color,
                topLeft = Offset(left, span.start),
                size = Size(right - left, span.endInclusive - span.start),
                cornerRadius = CornerRadius(cornerRadius),
            )
        }
    }
}
