package com.plcoding.marky.core.designsystem.components.toolbar

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plcoding.marky.core.designsystem.components.icon.MarkyIcon
import com.plcoding.marky.core.designsystem.components.list.MarkyDivider
import com.plcoding.marky.core.designsystem.components.text.MarkyText
import com.plcoding.marky.core.designsystem.icon.MarkyIcons
import com.plcoding.marky.core.designsystem.preview.MarkyPreview
import com.plcoding.marky.core.designsystem.preview.PressedInteractionSource
import com.plcoding.marky.core.designsystem.theme.MarkyShapes
import com.plcoding.marky.core.designsystem.theme.MarkyTheme

/**
 * Formatting toolbar that sits on top of the keyboard in Write mode. Fill it with
 * [MarkyToolbarButton]s in the design's order: H, B, I, list, checklist, quote, code, link.
 */
@Composable
fun MarkyToolbar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MarkyTheme.colors.background),
    ) {
        MarkyDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            content = content,
        )
    }
}

/**
 * Toolbar button with a letter label, like H, B or I.
 *
 * @param isActive The cursor or selection is already inside this format.
 */
@Composable
fun RowScope.MarkyToolbarButton(
    label: String,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    enabled: Boolean = true,
    italic: Boolean = false,
    interactionSource: MutableInteractionSource? = null,
) {
    ToolbarButton(
        contentDescription = contentDescription,
        onClick = onClick,
        modifier = modifier.weight(1f),
        isActive = isActive,
        enabled = enabled,
        interactionSource = interactionSource,
    ) { color ->
        MarkyText(
            text = label,
            style = MarkyTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                fontStyle = if (italic) FontStyle.Italic else FontStyle.Normal,
                fontSize = 15.sp,
                lineHeight = 15.sp,
            ),
            color = color,
            maxLines = 1,
        )
    }
}

/**
 * Toolbar button with a 16 dp icon.
 *
 * @param isActive The cursor or selection is already inside this format.
 */
@Composable
fun RowScope.MarkyToolbarButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
) {
    ToolbarButton(
        contentDescription = contentDescription,
        onClick = onClick,
        modifier = modifier.weight(1f),
        isActive = isActive,
        enabled = enabled,
        interactionSource = interactionSource,
    ) { color ->
        MarkyIcon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            size = 16.dp,
            strokeWidth = 2f,
        )
    }
}

@Composable
private fun ToolbarButton(
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier,
    isActive: Boolean,
    enabled: Boolean,
    interactionSource: MutableInteractionSource?,
    content: @Composable (contentColor: Color) -> Unit,
) {
    val colors = MarkyTheme.colors
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val isPressed by source.collectIsPressedAsState()
    val container = when {
        isActive -> colors.highlight
        isPressed && enabled -> colors.surfacePressed
        else -> colors.surface
    }
    val contentColor = when {
        !enabled -> colors.inkFaint
        isActive -> colors.onHighlight
        else -> colors.ink
    }
    Surface(
        onClick = onClick,
        modifier = modifier
            .height(40.dp)
            .semantics {
                this.contentDescription = contentDescription
                selected = isActive
            },
        enabled = enabled,
        shape = MarkyShapes.small,
        color = container,
        contentColor = contentColor,
        interactionSource = source,
    ) {
        Box(contentAlignment = Alignment.Center) {
            content(contentColor)
        }
    }
}

@Composable
private fun FormattingToolbar(
    activeIndex: Int?,
    enabled: Boolean = true,
    pressedIndex: Int? = null,
) {
    @Composable
    fun source(index: Int) = if (index == pressedIndex) remember { PressedInteractionSource() } else null

    MarkyToolbar(modifier = Modifier.width(360.dp)) {
        MarkyToolbarButton(label = "H", contentDescription = "Heading", onClick = {}, isActive = activeIndex == 0, enabled = enabled, interactionSource = source(0))
        MarkyToolbarButton(label = "B", contentDescription = "Bold", onClick = {}, isActive = activeIndex == 1, enabled = enabled, interactionSource = source(1))
        MarkyToolbarButton(label = "I", contentDescription = "Italic", onClick = {}, isActive = activeIndex == 2, enabled = enabled, italic = true, interactionSource = source(2))
        MarkyToolbarButton(icon = MarkyIcons.BulletList, contentDescription = "Bullet list", onClick = {}, isActive = activeIndex == 3, enabled = enabled, interactionSource = source(3))
        MarkyToolbarButton(icon = MarkyIcons.Checklist, contentDescription = "Checklist", onClick = {}, isActive = activeIndex == 4, enabled = enabled, interactionSource = source(4))
        MarkyToolbarButton(icon = MarkyIcons.Quote, contentDescription = "Quote", onClick = {}, isActive = activeIndex == 5, enabled = enabled, interactionSource = source(5))
        MarkyToolbarButton(icon = MarkyIcons.Code, contentDescription = "Code", onClick = {}, isActive = activeIndex == 6, enabled = enabled, interactionSource = source(6))
        MarkyToolbarButton(icon = MarkyIcons.Link, contentDescription = "Link", onClick = {}, isActive = activeIndex == 7, enabled = enabled, interactionSource = source(7))
    }
}

@Composable
private fun ToolbarStates() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        FormattingToolbar(activeIndex = 4)
        FormattingToolbar(activeIndex = 2, pressedIndex = 1)
        FormattingToolbar(activeIndex = null, enabled = false)
    }
}

@Preview(name = "Toolbar · light")
@Composable
private fun ToolbarLightPreview() {
    MarkyPreview(padding = 0.dp) { ToolbarStates() }
}

@Preview(name = "Toolbar · dark")
@Composable
private fun ToolbarDarkPreview() {
    MarkyPreview(darkTheme = true, padding = 0.dp) { ToolbarStates() }
}
