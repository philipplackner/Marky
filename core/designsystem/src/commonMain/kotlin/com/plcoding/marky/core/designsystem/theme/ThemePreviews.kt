package com.plcoding.marky.core.designsystem.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.plcoding.marky.core.designsystem.components.text.MarkyText
import com.plcoding.marky.core.designsystem.preview.MarkyPreview

@Composable
private fun ColorSheet() {
    val colors = MarkyTheme.colors
    val tokens = listOf(
        "background" to colors.background,
        "raised" to colors.raised,
        "surface" to colors.surface,
        "surfacePressed" to colors.surfacePressed,
        "divider" to colors.divider,
        "outline" to colors.outline,
        "ink" to colors.ink,
        "inkMuted" to colors.inkMuted,
        "inkFaint" to colors.inkFaint,
        "highlight" to colors.highlight,
        "highlightPressed" to colors.highlightPressed,
        "highlightSoft" to colors.highlightSoft,
        "swash" to colors.swash,
        "onHighlight" to colors.onHighlight,
        "quoteMark" to colors.quoteMark,
        "inverse" to colors.inverse,
        "inversePressed" to colors.inversePressed,
        "onInverse" to colors.onInverse,
        "inverseAccent" to colors.inverseAccent,
        "codeBackground" to colors.codeBackground,
        "codeInk" to colors.codeInk,
        "danger" to colors.danger,
        "dangerPressed" to colors.dangerPressed,
        "onDanger" to colors.onDanger,
        "selection" to colors.selection,
        "scrim" to colors.scrim,
    )
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        tokens.forEach { (name, color) ->
            ColorSwatch(name = name, color = color)
        }
    }
}

@Composable
private fun ColorSwatch(name: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .border(1.dp, MarkyTheme.colors.outline, MarkyShapes.small)
                .background(color, MarkyShapes.small),
        )
        MarkyText(text = name, style = MarkyTheme.typography.code)
    }
}

@Composable
private fun TypeSheet() {
    val type = MarkyTheme.typography
    val styles: List<Pair<String, TextStyle>> = listOf(
        "Notes" to type.display,
        "Trip to Lisbon" to type.titleLarge,
        "No notes yet" to type.titleMedium,
        "Weekly groceries" to type.titleSmall,
        "Heading 1" to type.heading1,
        "Heading 2" to type.heading2,
        "Heading 3" to type.heading3,
        "Flights are booked." to type.bodyLarge,
        "Oat milk, lemons, sourdough" to type.bodyMedium,
        "New note" to type.labelLarge,
        "Pinned" to type.labelMedium,
        "4 min" to type.labelSmall,
        "Seat 14C -> Gate 07:55" to type.code,
    )
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        styles.forEach { (sample, style) ->
            MarkyText(text = sample, style = style)
        }
    }
}

@Preview(name = "Colors · light")
@Composable
private fun ColorsLightPreview() {
    MarkyPreview { ColorSheet() }
}

@Preview(name = "Colors · dark")
@Composable
private fun ColorsDarkPreview() {
    MarkyPreview(darkTheme = true) { ColorSheet() }
}

@Preview(name = "Typography · light")
@Composable
private fun TypographyLightPreview() {
    MarkyPreview { TypeSheet() }
}

@Preview(name = "Typography · dark")
@Composable
private fun TypographyDarkPreview() {
    MarkyPreview(darkTheme = true) { TypeSheet() }
}
