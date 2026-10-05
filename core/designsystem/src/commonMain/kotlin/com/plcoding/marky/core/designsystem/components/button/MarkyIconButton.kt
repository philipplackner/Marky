package com.plcoding.marky.core.designsystem.components.button

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.plcoding.marky.core.designsystem.components.icon.MarkyIcon
import com.plcoding.marky.core.designsystem.icon.MarkyIcons
import com.plcoding.marky.core.designsystem.preview.MarkyPreview
import com.plcoding.marky.core.designsystem.preview.PressedInteractionSource
import com.plcoding.marky.core.designsystem.theme.MarkyShapes
import com.plcoding.marky.core.designsystem.theme.MarkyTheme

enum class MarkyIconButtonStyle {
    /** Sits on the `surface` color. */
    Filled,

    /** No background until pressed. */
    Plain,
}

/**
 * Round 40 × 40 icon button with a 24 dp icon.
 */
@Composable
fun MarkyIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: MarkyIconButtonStyle = MarkyIconButtonStyle.Filled,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
) {
    val colors = MarkyTheme.colors
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val isPressed by source.collectIsPressedAsState()
    val container = when (style) {
        MarkyIconButtonStyle.Filled -> if (isPressed && enabled) colors.surfacePressed else colors.surface
        MarkyIconButtonStyle.Plain -> if (isPressed && enabled) colors.surface else Color.Transparent
    }
    FilledTonalIconButton(
        onClick = onClick,
        modifier = modifier.size(40.dp),
        enabled = enabled,
        shape = MarkyShapes.pill,
        colors = IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = container,
            contentColor = colors.ink,
            disabledContainerColor = container,
            disabledContentColor = colors.inkFaint,
        ),
        interactionSource = source,
    ) {
        MarkyIcon(imageVector = icon, contentDescription = contentDescription)
    }
}

/**
 * Icon button that toggles, like the pin button. Turns yellow when [checked].
 */
@Composable
fun MarkyIconToggleButton(
    icon: ImageVector,
    contentDescription: String?,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
) {
    val colors = MarkyTheme.colors
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val isPressed by source.collectIsPressedAsState()
    val showPressed = isPressed && enabled
    FilledIconToggleButton(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier.size(40.dp),
        enabled = enabled,
        shape = MarkyShapes.pill,
        colors = IconButtonDefaults.filledIconToggleButtonColors(
            containerColor = if (showPressed) colors.surfacePressed else colors.surface,
            contentColor = colors.ink,
            checkedContainerColor = if (showPressed) colors.highlightPressed else colors.highlight,
            checkedContentColor = colors.onHighlight,
            disabledContainerColor = colors.surface,
            disabledContentColor = colors.inkFaint,
        ),
        interactionSource = source,
    ) {
        MarkyIcon(imageVector = icon, contentDescription = contentDescription)
    }
}

@Composable
private fun IconButtonStates() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        MarkyIconButtonStyle.entries.forEach { style ->
            val icon = if (style == MarkyIconButtonStyle.Filled) MarkyIcons.Import else MarkyIcons.Search
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MarkyIconButton(icon = icon, contentDescription = null, onClick = {}, style = style)
                MarkyIconButton(
                    icon = icon,
                    contentDescription = null,
                    onClick = {},
                    style = style,
                    interactionSource = remember { PressedInteractionSource() },
                )
                MarkyIconButton(icon = icon, contentDescription = null, onClick = {}, style = style, enabled = false)
            }
        }
    }
}

@Composable
private fun ToggleStates() {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        MarkyIconToggleButton(icon = MarkyIcons.Pin, contentDescription = null, checked = false, onCheckedChange = {})
        MarkyIconToggleButton(icon = MarkyIcons.Pin, contentDescription = null, checked = true, onCheckedChange = {})
        MarkyIconToggleButton(
            icon = MarkyIcons.Pin,
            contentDescription = null,
            checked = true,
            onCheckedChange = {},
            interactionSource = remember { PressedInteractionSource() },
        )
    }
}

@Preview(name = "Icon buttons · light")
@Composable
private fun IconButtonsLightPreview() {
    MarkyPreview { IconButtonStates() }
}

@Preview(name = "Icon buttons · dark")
@Composable
private fun IconButtonsDarkPreview() {
    MarkyPreview(darkTheme = true) { IconButtonStates() }
}

@Preview(name = "Pin toggle · light")
@Composable
private fun PinToggleLightPreview() {
    MarkyPreview { ToggleStates() }
}

@Preview(name = "Pin toggle · dark")
@Composable
private fun PinToggleDarkPreview() {
    MarkyPreview(darkTheme = true) { ToggleStates() }
}
