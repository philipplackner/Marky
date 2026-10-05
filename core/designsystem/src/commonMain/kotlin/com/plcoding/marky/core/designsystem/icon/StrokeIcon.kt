package com.plcoding.marky.core.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.VectorPath
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

internal const val DefaultIconStroke = 1.8f

internal fun strokeIcon(name: String, vararg pathData: String): ImageVector =
    ImageVector.Builder(
        name = name,
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        pathData.forEach { data ->
            addPath(
                pathData = addPathNodes(data),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = DefaultIconStroke,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            )
        }
    }.build()

/**
 * Copy of this icon with a different stroke width, in viewport units. The design
 * draws icons inside buttons (2.2), tool buttons and the tile pin mark (2) bolder.
 */
internal fun ImageVector.withStrokeWidth(strokeWidth: Float): ImageVector {
    if (strokeWidth == DefaultIconStroke) return this
    return ImageVector.Builder(
        name = name,
        defaultWidth = defaultWidth,
        defaultHeight = defaultHeight,
        viewportWidth = viewportWidth,
        viewportHeight = viewportHeight,
    ).apply {
        root.filterIsInstance<VectorPath>().forEach { path ->
            addPath(
                pathData = path.pathData,
                pathFillType = path.pathFillType,
                name = path.name,
                fill = path.fill,
                fillAlpha = path.fillAlpha,
                stroke = path.stroke,
                strokeAlpha = path.strokeAlpha,
                strokeLineWidth = strokeWidth,
                strokeLineCap = path.strokeLineCap,
                strokeLineJoin = path.strokeLineJoin,
                strokeLineMiter = path.strokeLineMiter,
            )
        }
    }.build()
}
