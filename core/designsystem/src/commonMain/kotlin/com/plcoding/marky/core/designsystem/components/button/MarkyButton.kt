package com.plcoding.marky.core.designsystem.components.button

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plcoding.marky.core.designsystem.components.icon.MarkyIcon
import com.plcoding.marky.core.designsystem.components.text.MarkyText
import com.plcoding.marky.core.designsystem.icon.MarkyIcons
import com.plcoding.marky.core.designsystem.preview.MarkyPreview
import com.plcoding.marky.core.designsystem.preview.PressedInteractionSource
import com.plcoding.marky.core.designsystem.theme.MarkyColors
import com.plcoding.marky.core.designsystem.theme.MarkyShapes
import com.plcoding.marky.core.designsystem.theme.MarkyTheme

enum class MarkyButtonStyle {
    /** The one main action on a screen. Inverse pill. */
    Primary,

    /** Alternatives to the main action. */
    Secondary,

    /** Only the confirm button of the delete dialog. */
    Danger,

    /** Low-emphasis actions. */
    Text,
}

enum class MarkyButtonSize {
    /** Height 48. */
    Regular,

    /** Height 40. */
    Small,
}

/**
 * Pill button. Pressed scales to 97 % and darkens.
 *
 * @param icon Optional leading icon, drawn at 20 dp.
 */
@Composable
fun MarkyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: MarkyButtonStyle = MarkyButtonStyle.Primary,
    size: MarkyButtonSize = MarkyButtonSize.Regular,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
) {
    val colors = MarkyTheme.colors
    val isSmall = size == MarkyButtonSize.Small
    MarkyButtonBase(
        text = text,
        onClick = onClick,
        modifier = modifier,
        palette = style.palette(colors),
        height = if (isSmall) 40.dp else 48.dp,
        horizontalPadding = when {
            style == MarkyButtonStyle.Text -> 14.dp
            isSmall -> 16.dp
            else -> 22.dp
        },
        textStyle = MarkyTheme.typography.labelLarge.let { base ->
            if (isSmall) base.copy(fontSize = 14.sp) else base
        },
        icon = icon,
        enabled = enabled,
        interactionSource = interactionSource,
    )
}

internal class ButtonPalette(
    val container: Color,
    val containerPressed: Color,
    val content: Color,
    val icon: Color,
    val disabledContainer: Color,
    val disabledContent: Color,
)

private fun MarkyButtonStyle.palette(colors: MarkyColors): ButtonPalette = when (this) {
    MarkyButtonStyle.Primary -> ButtonPalette(
        container = colors.inverse,
        containerPressed = colors.inversePressed,
        content = colors.onInverse,
        icon = colors.inverseAccent,
        disabledContainer = colors.surface,
        disabledContent = colors.inkFaint,
    )
    MarkyButtonStyle.Secondary -> ButtonPalette(
        container = colors.surface,
        containerPressed = colors.surfacePressed,
        content = colors.ink,
        icon = colors.ink,
        disabledContainer = colors.surface,
        disabledContent = colors.inkFaint,
    )
    MarkyButtonStyle.Danger -> ButtonPalette(
        container = colors.danger,
        containerPressed = colors.dangerPressed,
        content = colors.onDanger,
        icon = colors.onDanger,
        disabledContainer = colors.surface,
        disabledContent = colors.inkFaint,
    )
    MarkyButtonStyle.Text -> ButtonPalette(
        container = Color.Transparent,
        containerPressed = colors.surface,
        content = colors.ink,
        icon = colors.ink,
        disabledContainer = Color.Transparent,
        disabledContent = colors.inkFaint,
    )
}

@Composable
internal fun MarkyButtonBase(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier,
    palette: ButtonPalette,
    height: Dp,
    horizontalPadding: Dp,
    textStyle: TextStyle,
    icon: ImageVector?,
    enabled: Boolean,
    interactionSource: MutableInteractionSource?,
    shape: Shape = MarkyShapes.pill,
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val isPressed by source.collectIsPressedAsState()
    val showPressed = enabled && isPressed
    val scale by animateFloatAsState(
        targetValue = if (showPressed) 0.97f else 1f,
        animationSpec = tween(durationMillis = 120),
    )
    Button(
        onClick = onClick,
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .height(height),
        enabled = enabled,
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (showPressed) palette.containerPressed else palette.container,
            contentColor = palette.content,
            disabledContainerColor = palette.disabledContainer,
            disabledContentColor = palette.disabledContent,
        ),
        elevation = null,
        contentPadding = PaddingValues(horizontal = horizontalPadding),
        interactionSource = source,
    ) {
        Row(horizontalArrangement = Arrangement.Center) {
            if (icon != null) {
                MarkyIcon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (enabled) palette.icon else palette.disabledContent,
                    size = 20.dp,
                    strokeWidth = 2.2f,
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            MarkyText(
                text = text,
                style = textStyle,
                color = if (enabled) palette.content else palette.disabledContent,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun ButtonStates(style: MarkyButtonStyle, text: String, icon: ImageVector?) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        MarkyButton(text = text, onClick = {}, style = style, icon = icon)
        MarkyButton(
            text = text,
            onClick = {},
            style = style,
            icon = icon,
            interactionSource = remember { PressedInteractionSource() },
        )
        MarkyButton(text = text, onClick = {}, style = style, icon = icon, enabled = false)
    }
}

@Preview(name = "Primary · light")
@Composable
private fun PrimaryLightPreview() {
    MarkyPreview { ButtonStates(MarkyButtonStyle.Primary, "New note", MarkyIcons.Plus) }
}

@Preview(name = "Primary · dark")
@Composable
private fun PrimaryDarkPreview() {
    MarkyPreview(darkTheme = true) { ButtonStates(MarkyButtonStyle.Primary, "New note", MarkyIcons.Plus) }
}

@Preview(name = "Secondary · light")
@Composable
private fun SecondaryLightPreview() {
    MarkyPreview { ButtonStates(MarkyButtonStyle.Secondary, "Import .md file", MarkyIcons.Import) }
}

@Preview(name = "Secondary · dark")
@Composable
private fun SecondaryDarkPreview() {
    MarkyPreview(darkTheme = true) { ButtonStates(MarkyButtonStyle.Secondary, "Import .md file", MarkyIcons.Import) }
}

@Preview(name = "Danger · light")
@Composable
private fun DangerLightPreview() {
    MarkyPreview { ButtonStates(MarkyButtonStyle.Danger, "Delete", null) }
}

@Preview(name = "Danger · dark")
@Composable
private fun DangerDarkPreview() {
    MarkyPreview(darkTheme = true) { ButtonStates(MarkyButtonStyle.Danger, "Delete", null) }
}

@Preview(name = "Text · light")
@Composable
private fun TextLightPreview() {
    MarkyPreview { ButtonStates(MarkyButtonStyle.Text, "Cancel", null) }
}

@Preview(name = "Text · dark")
@Composable
private fun TextDarkPreview() {
    MarkyPreview(darkTheme = true) { ButtonStates(MarkyButtonStyle.Text, "Cancel", null) }
}

@Composable
private fun SmallButtons() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        MarkyButton(text = "Start writing", onClick = {}, size = MarkyButtonSize.Small)
        MarkyButton(
            text = "Choose file",
            onClick = {},
            style = MarkyButtonStyle.Secondary,
            size = MarkyButtonSize.Small,
        )
    }
}

@Preview(name = "Small · light")
@Composable
private fun SmallLightPreview() {
    MarkyPreview { SmallButtons() }
}

@Preview(name = "Small · dark")
@Composable
private fun SmallDarkPreview() {
    MarkyPreview(darkTheme = true) { SmallButtons() }
}
