package com.plcoding.marky.core.designsystem.components.list

import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.plcoding.marky.core.designsystem.theme.MarkyTheme

/**
 * 1 dp line between rows and above the formatting toolbar.
 *
 * @param visible Keeps the space but hides the line, like the divider under a just-imported row.
 */
@Composable
fun MarkyDivider(
    modifier: Modifier = Modifier,
    visible: Boolean = true,
) {
    HorizontalDivider(
        modifier = modifier,
        thickness = 1.dp,
        color = if (visible) MarkyTheme.colors.divider else Color.Transparent,
    )
}
