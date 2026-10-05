package com.plcoding.marky.core.designsystem.components.snackbar

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plcoding.marky.core.designsystem.components.text.MarkyText
import com.plcoding.marky.core.designsystem.preview.MarkyPreview
import com.plcoding.marky.core.designsystem.preview.PressedInteractionSource
import com.plcoding.marky.core.designsystem.theme.MarkyShapes
import com.plcoding.marky.core.designsystem.theme.MarkyTheme
import com.plcoding.marky.core.designsystem.theme.markyShadow

/**
 * Shows the current snackbar of [hostState]. The screen places it at the bottom,
 * 12 dp from the sides and above the FAB position.
 */
@Composable
fun MarkySnackbarHost(
    hostState: MarkySnackbarHostState,
    modifier: Modifier = Modifier,
) {
    val data = hostState.currentSnackbar ?: return
    MarkySnackbar(
        message = data.message,
        actionLabel = data.actionLabel,
        onActionClick = data::performAction,
        modifier = modifier,
    )
}

/**
 * Short feedback in inverse colors, with an optional action.
 */
@Composable
fun MarkySnackbar(
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onActionClick: () -> Unit = {},
    actionInteractionSource: MutableInteractionSource? = null,
) {
    val colors = MarkyTheme.colors
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .markyShadow(MarkyShapes.large, colors.shadow),
        shape = MarkyShapes.large,
        color = colors.inverse,
        contentColor = colors.onInverse,
    ) {
        Row(
            modifier = Modifier
                .heightIn(min = 52.dp)
                .padding(start = 16.dp, top = 6.dp, end = 6.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            MarkyText(
                text = message,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp),
                style = MarkyTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = colors.onInverse,
            )
            if (actionLabel != null) {
                // Clickable M3 surfaces grow to a 48 dp touch target, which would make the snackbar taller than designed.
                CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
                    SnackbarAction(
                        label = actionLabel,
                        onClick = onActionClick,
                        interactionSource = actionInteractionSource,
                    )
                }
            }
        }
    }
}

@Composable
private fun SnackbarAction(
    label: String,
    onClick: () -> Unit,
    interactionSource: MutableInteractionSource?,
) {
    val colors = MarkyTheme.colors
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val isPressed by source.collectIsPressedAsState()
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = if (isPressed) colors.inversePressed else Color.Transparent,
        contentColor = colors.inverseAccent,
        interactionSource = source,
    ) {
        MarkyText(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            style = MarkyTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                lineHeight = 14.sp,
            ),
            color = colors.inverseAccent,
            maxLines = 1,
        )
    }
}

@Composable
private fun SnackbarStates() {
    Column(
        modifier = Modifier.width(340.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        MarkySnackbar(
            message = "Couldn't import \"recipes.pdf\". Choose a .md file.",
            actionLabel = "Choose file",
        )
        MarkySnackbar(
            message = "Couldn't import \"recipes.pdf\". Choose a .md file.",
            actionLabel = "Choose file",
            actionInteractionSource = remember { PressedInteractionSource() },
        )
        MarkySnackbar(message = "Couldn't read \"ideas.md\". The file may be damaged.")
        MarkySnackbar(message = "Exported \"Trip to Lisbon.md\"")
        MarkySnackbar(message = "Couldn't export \"Trip to Lisbon.md\". Try again.")
    }
}

@Preview(name = "Snackbar · light")
@Composable
private fun SnackbarLightPreview() {
    MarkyPreview(padding = 24.dp) { SnackbarStates() }
}

@Preview(name = "Snackbar · dark")
@Composable
private fun SnackbarDarkPreview() {
    MarkyPreview(darkTheme = true, padding = 24.dp) { SnackbarStates() }
}
