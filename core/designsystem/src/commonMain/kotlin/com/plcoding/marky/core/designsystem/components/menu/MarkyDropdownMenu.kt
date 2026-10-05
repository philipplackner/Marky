package com.plcoding.marky.core.designsystem.components.menu

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenuPopup
import androidx.compose.material3.MenuAnchorPosition
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.plcoding.marky.core.designsystem.components.icon.MarkyIcon
import com.plcoding.marky.core.designsystem.components.text.MarkyText
import com.plcoding.marky.core.designsystem.icon.MarkyIcons
import com.plcoding.marky.core.designsystem.preview.MarkyPreview
import com.plcoding.marky.core.designsystem.preview.PressedInteractionSource
import com.plcoding.marky.core.designsystem.theme.MarkyShapes
import com.plcoding.marky.core.designsystem.theme.MarkyTheme

/**
 * Overflow menu. Place it in a `Box` together with the button that opens it; it
 * shows below that button. Fill it with [MarkyMenuItem]s and [MarkyMenuDivider]s.
 */
@Composable
fun MarkyDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    offset: DpOffset = DpOffset(0.dp, 0.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    DropdownMenuPopup(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        popupPositionProvider = MenuDefaults.rememberDropdownMenuPopupPositionProvider(
            dropdownMenuAnchorPosition = MenuAnchorPosition.Below,
            offset = offset,
        ),
    ) {
        MenuCard(content = content)
    }
}

@Composable
private fun MenuCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = MarkyTheme.colors
    Surface(
        modifier = modifier.width(220.dp),
        shape = MarkyShapes.extraLarge,
        color = colors.raised,
        contentColor = colors.ink,
        border = BorderStroke(1.dp, colors.divider),
        shadowElevation = 12.dp,
    ) {
        Column(
            modifier = Modifier.padding(6.dp),
            content = content,
        )
    }
}

/**
 * One entry of a [MarkyDropdownMenu].
 *
 * @param isDestructive Red text and icon, for Delete.
 */
@Composable
fun MarkyMenuItem(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDestructive: Boolean = false,
    interactionSource: MutableInteractionSource? = null,
) {
    val colors = MarkyTheme.colors
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val isPressed by source.collectIsPressedAsState()
    val contentColor = if (isDestructive) colors.danger else colors.ink
    DropdownMenuItem(
        text = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                MarkyIcon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    size = 20.dp,
                )
                MarkyText(
                    text = text,
                    style = MarkyTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
                    color = contentColor,
                    maxLines = 1,
                )
            }
        },
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(46.dp)
            .clip(MarkyShapes.small)
            .background(if (isPressed) colors.surface else Color.Transparent),
        contentPadding = PaddingValues(horizontal = 12.dp),
        interactionSource = source,
    )
}

/**
 * Line that separates the destructive item from the rest.
 */
@Composable
fun MarkyMenuDivider(modifier: Modifier = Modifier) {
    Spacer(
        modifier = modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .fillMaxWidth()
            .height(1.dp)
            .background(MarkyTheme.colors.divider),
    )
}

@Composable
private fun MenuPreviewContent(isPinned: Boolean, pressedExport: Boolean) {
    MenuCard {
        MarkyMenuItem(
            text = if (isPinned) "Unpin note" else "Pin note",
            icon = if (isPinned) MarkyIcons.PinOff else MarkyIcons.Pin,
            onClick = {},
        )
        MarkyMenuItem(
            text = "Export as .md",
            icon = MarkyIcons.Export,
            onClick = {},
            interactionSource = if (pressedExport) remember { PressedInteractionSource() } else null,
        )
        MarkyMenuDivider()
        MarkyMenuItem(text = "Delete note", icon = MarkyIcons.Trash, onClick = {}, isDestructive = true)
    }
}

@Preview(name = "Menu · unpinned, export pressed · light")
@Composable
private fun MenuUnpinnedLightPreview() {
    MarkyPreview(padding = 24.dp) { MenuPreviewContent(isPinned = false, pressedExport = true) }
}

@Preview(name = "Menu · unpinned, export pressed · dark")
@Composable
private fun MenuUnpinnedDarkPreview() {
    MarkyPreview(darkTheme = true, padding = 24.dp) { MenuPreviewContent(isPinned = false, pressedExport = true) }
}

@Preview(name = "Menu · pinned · light")
@Composable
private fun MenuPinnedLightPreview() {
    MarkyPreview(padding = 24.dp) { MenuPreviewContent(isPinned = true, pressedExport = false) }
}

@Preview(name = "Menu · pinned · dark")
@Composable
private fun MenuPinnedDarkPreview() {
    MarkyPreview(darkTheme = true, padding = 24.dp) { MenuPreviewContent(isPinned = true, pressedExport = false) }
}
