package com.plcoding.marky.core.designsystem.theme

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.dropShadow

/**
 * The `shadow` token: FAB and snackbar. Pass [MarkyColors.shadow].
 */
fun Modifier.markyShadow(shape: Shape, color: Color): Modifier = dropShadow(
    shape = shape,
    shadow = Shadow(
        radius = 30.dp,
        color = color,
        spread = (-12).dp,
        offset = DpOffset(0.dp, 14.dp),
    ),
)

/**
 * The `shadowLarge` token: menu and dialog. Pass [MarkyColors.shadowLarge].
 */
fun Modifier.markyShadowLarge(shape: Shape, color: Color): Modifier = dropShadow(
    shape = shape,
    shadow = Shadow(
        radius = 48.dp,
        color = color,
        spread = (-16).dp,
        offset = DpOffset(0.dp, 24.dp),
    ),
)
