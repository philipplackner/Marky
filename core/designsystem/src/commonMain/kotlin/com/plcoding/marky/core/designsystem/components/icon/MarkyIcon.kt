package com.plcoding.marky.core.designsystem.components.icon

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.plcoding.marky.core.designsystem.icon.DefaultIconStroke
import com.plcoding.marky.core.designsystem.icon.withStrokeWidth

/**
 * Draws one of the [com.plcoding.marky.core.designsystem.icon.MarkyIcons].
 *
 * @param size 24 in bars and buttons, 20 in menus and button icons, 16 for the tile pin mark.
 * @param strokeWidth Stroke in 24-grid units. Keep the default unless the design asks for a bolder icon.
 */
@Composable
fun MarkyIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    size: Dp = 24.dp,
    strokeWidth: Float = DefaultIconStroke,
) {
    val icon = remember(imageVector, strokeWidth) { imageVector.withStrokeWidth(strokeWidth) }
    Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        modifier = modifier.size(size),
        tint = tint,
    )
}
