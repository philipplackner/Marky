package com.plcoding.marky.core.designsystem.icon

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp
import com.plcoding.marky.core.designsystem.theme.MarkyColors
import com.plcoding.marky.core.designsystem.theme.MarkyTheme

/**
 * Line illustrations for empty states (120 × 96). They are drawn in theme colors,
 * so show them with `Image`, not `Icon`, to keep the yellow stroke.
 */
object MarkyIllustrations {
    /** Notes list without notes. */
    val NoNotes: ImageVector
        @Composable get() = rememberIllustration(
            "NoNotes",
            Stroke.Paper to "M42 8h36a8 8 0 0 1 8 8v56a8 8 0 0 1 -8 8h-36a8 8 0 0 1 -8 -8v-56a8 8 0 0 1 8 -8z",
            Stroke.Swash to "M42 50l36-3",
            Stroke.Ink to "M44 26h32M44 38h22M44 50h28M44 62h16",
        )

    /** Note without text in Read mode. */
    val EmptyNote: ImageVector
        @Composable get() = rememberIllustration(
            "EmptyNote",
            Stroke.Paper to "M34 8h36a8 8 0 0 1 8 8v56a8 8 0 0 1 -8 8h-36a8 8 0 0 1 -8 -8v-56a8 8 0 0 1 8 -8z",
            Stroke.Swash to "M36 28l26-2",
            Stroke.Ink to "M36 28h26",
            Stroke.Paper to "M72 76l20-34 8 4.6-20 34-9.5 4.4z",
            Stroke.Ink to "M88 49l8 4.6",
        )

    /** Search screen before typing. */
    val SearchStart: ImageVector
        @Composable get() = rememberIllustration(
            "SearchStart",
            Stroke.Swash to "M40 48l24-3",
            Stroke.Ink to "M30 42a22 22 0 1 0 44 0a22 22 0 1 0 -44 0z",
            Stroke.Ink to "M68 58l20 20",
        )

    /** Search without results. */
    val SearchNoResults: ImageVector
        @Composable get() = rememberIllustration(
            "SearchNoResults",
            Stroke.Swash to "M26 84l34-2",
            Stroke.Ink to "M30 42a22 22 0 1 0 44 0a22 22 0 1 0 -44 0z",
            Stroke.Ink to "M68 58l20 20",
            Stroke.Ink to "M42 44c3-4 6 4 10 0s6 4 10 0",
        )

    private enum class Stroke { Ink, Paper, Swash }

    @Composable
    private fun rememberIllustration(name: String, vararg parts: Pair<Stroke, String>): ImageVector {
        val colors = MarkyTheme.colors
        return remember(name, colors) { buildIllustration(name, colors, parts.toList()) }
    }

    private fun buildIllustration(
        name: String,
        colors: MarkyColors,
        parts: List<Pair<Stroke, String>>,
    ): ImageVector = ImageVector.Builder(
        name = name,
        defaultWidth = 120.dp,
        defaultHeight = 96.dp,
        viewportWidth = 120f,
        viewportHeight = 96f,
    ).apply {
        parts.forEach { (stroke, data) ->
            val isSwash = stroke == Stroke.Swash
            addPath(
                pathData = addPathNodes(data),
                fill = if (stroke == Stroke.Paper) SolidColor(colors.background) else null,
                stroke = SolidColor(if (isSwash) colors.highlight else colors.ink),
                strokeLineWidth = if (isSwash) 10f else 2.4f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            )
        }
    }.build()
}
