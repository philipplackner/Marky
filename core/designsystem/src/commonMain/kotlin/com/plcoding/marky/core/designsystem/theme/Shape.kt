package com.plcoding.marky.core.designsystem.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

/**
 * Corner radii. Bigger elements get rounder corners; anything tappable that holds
 * a single label is a full pill.
 */
object MarkyShapes {
    /** 6 — inline code. */
    val extraSmall = RoundedCornerShape(6.dp)

    /** 12 — tool buttons, menu items. */
    val small = RoundedCornerShape(12.dp)

    /** 14 — search field. */
    val medium = RoundedCornerShape(14.dp)

    /** 16 — quote, code block, snackbar. */
    val large = RoundedCornerShape(16.dp)

    /** 18 — pinned tile, menu. */
    val extraLarge = RoundedCornerShape(18.dp)

    /** 28 — dialog. */
    val extraExtraLarge = RoundedCornerShape(28.dp)

    /** Buttons, toggle, icon buttons. */
    val pill = CircleShape
}
