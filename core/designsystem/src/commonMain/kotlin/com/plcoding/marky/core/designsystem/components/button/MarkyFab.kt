package com.plcoding.marky.core.designsystem.components.button

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plcoding.marky.core.designsystem.icon.MarkyIcons
import com.plcoding.marky.core.designsystem.preview.MarkyPreview
import com.plcoding.marky.core.designsystem.preview.PressedInteractionSource
import com.plcoding.marky.core.designsystem.theme.MarkyShapes
import com.plcoding.marky.core.designsystem.theme.MarkyTheme
import com.plcoding.marky.core.designsystem.theme.markyShadow

/**
 * Extended floating "New note" button. Only on the notes list; the screen places it
 * centered, 34 dp above the bottom edge.
 */
@Composable
fun MarkyFab(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource? = null,
) {
    val colors = MarkyTheme.colors
    MarkyButtonBase(
        text = text,
        onClick = onClick,
        modifier = modifier.markyShadow(MarkyShapes.pill, colors.shadow),
        palette = ButtonPalette(
            container = colors.inverse,
            containerPressed = colors.inversePressed,
            content = colors.onInverse,
            icon = colors.inverseAccent,
            disabledContainer = colors.surface,
            disabledContent = colors.inkFaint,
        ),
        height = 54.dp,
        horizontalPadding = 24.dp,
        textStyle = MarkyTheme.typography.labelLarge.copy(fontSize = 15.5.sp),
        icon = icon,
        enabled = true,
        interactionSource = interactionSource,
    )
}

@Composable
private fun FabStates() {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        MarkyFab(text = "New note", icon = MarkyIcons.Plus, onClick = {})
        MarkyFab(
            text = "New note",
            icon = MarkyIcons.Plus,
            onClick = {},
            interactionSource = remember { PressedInteractionSource() },
        )
    }
}

@Preview(name = "FAB · light")
@Composable
private fun FabLightPreview() {
    MarkyPreview(padding = 32.dp) { FabStates() }
}

@Preview(name = "FAB · dark")
@Composable
private fun FabDarkPreview() {
    MarkyPreview(darkTheme = true, padding = 32.dp) { FabStates() }
}
