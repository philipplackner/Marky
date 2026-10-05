package com.plcoding.marky.core.designsystem.components.toggle

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plcoding.marky.core.designsystem.components.text.MarkyText
import com.plcoding.marky.core.designsystem.preview.MarkyPreview
import com.plcoding.marky.core.designsystem.preview.PressedInteractionSource
import com.plcoding.marky.core.designsystem.theme.MarkyShapes
import com.plcoding.marky.core.designsystem.theme.MarkyTheme

/**
 * Pill toggle between a few options, like Write / Read. The selected option uses
 * the inverse color; tapping it again does nothing.
 */
@Composable
fun MarkySegmentedToggle(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    SegmentedToggle(
        options = options,
        selectedIndex = selectedIndex,
        onSelect = onSelect,
        modifier = modifier,
        pressedIndex = null,
    )
}

@Composable
private fun SegmentedToggle(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (index: Int) -> Unit,
    modifier: Modifier,
    pressedIndex: Int?,
) {
    Surface(
        modifier = modifier,
        shape = MarkyShapes.pill,
        color = MarkyTheme.colors.surface,
    ) {
        Row(
            modifier = Modifier
                .padding(3.dp)
                .selectableGroup(),
        ) {
            options.forEachIndexed { index, label ->
                ToggleOption(
                    label = label,
                    selected = index == selectedIndex,
                    onClick = { if (index != selectedIndex) onSelect(index) },
                    interactionSource = if (index == pressedIndex) {
                        remember { PressedInteractionSource() }
                    } else {
                        remember { MutableInteractionSource() }
                    },
                )
            }
        }
    }
}

@Composable
private fun ToggleOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    interactionSource: MutableInteractionSource,
) {
    val colors = MarkyTheme.colors
    val isPressed by interactionSource.collectIsPressedAsState()
    val container = when {
        selected -> colors.inverse
        isPressed -> colors.surfacePressed
        else -> Color.Transparent
    }
    val content = when {
        selected -> colors.onInverse
        isPressed -> colors.ink
        else -> colors.inkMuted
    }
    Surface(
        selected = selected,
        onClick = onClick,
        modifier = Modifier.semantics { role = Role.Tab },
        shape = MarkyShapes.pill,
        color = container,
        contentColor = content,
        interactionSource = interactionSource,
    ) {
        MarkyText(
            text = label,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
            style = MarkyTheme.typography.labelMedium.copy(fontSize = 13.5.sp, lineHeight = 13.5.sp),
            color = content,
            maxLines = 1,
        )
    }
}

private val previewOptions = listOf("Write", "Read")

@Composable
private fun ToggleStates() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        MarkySegmentedToggle(options = previewOptions, selectedIndex = 0, onSelect = {})
        MarkySegmentedToggle(options = previewOptions, selectedIndex = 1, onSelect = {})
        SegmentedToggle(
            options = previewOptions,
            selectedIndex = 0,
            onSelect = {},
            modifier = Modifier,
            pressedIndex = 1,
        )
    }
}

@Preview(name = "Toggle · light")
@Composable
private fun ToggleLightPreview() {
    MarkyPreview { ToggleStates() }
}

@Preview(name = "Toggle · dark")
@Composable
private fun ToggleDarkPreview() {
    MarkyPreview(darkTheme = true) { ToggleStates() }
}
