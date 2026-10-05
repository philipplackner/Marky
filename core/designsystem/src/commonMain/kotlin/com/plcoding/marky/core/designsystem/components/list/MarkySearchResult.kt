package com.plcoding.marky.core.designsystem.components.list

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import com.plcoding.marky.core.designsystem.components.text.MarkyHighlightedText
import com.plcoding.marky.core.designsystem.generated.resources.Res
import com.plcoding.marky.core.designsystem.generated.resources.no_text_yet
import com.plcoding.marky.core.designsystem.generated.resources.untitled
import com.plcoding.marky.core.designsystem.preview.MarkyPreview
import com.plcoding.marky.core.designsystem.preview.PressedInteractionSource
import com.plcoding.marky.core.designsystem.theme.MarkySpacing
import com.plcoding.marky.core.designsystem.theme.MarkyTheme

/**
 * Search hit: title and a snippet of up to two lines, with every match marked in yellow.
 * Put a [MarkyDivider] between results.
 *
 * @param title null shows "Untitled" in muted.
 * @param snippet null shows "No text yet" in faint.
 * @param titleMatches Character ranges in [title] to mark.
 * @param snippetMatches Character ranges in [snippet] to mark.
 */
@Composable
fun MarkySearchResult(
    title: String?,
    snippet: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    titleMatches: List<IntRange> = emptyList(),
    snippetMatches: List<IntRange> = emptyList(),
    interactionSource: MutableInteractionSource? = null,
) {
    val colors = MarkyTheme.colors
    val typography = MarkyTheme.typography
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val isPressed by source.collectIsPressedAsState()
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        color = if (isPressed) colors.surface else Color.Transparent,
        contentColor = colors.ink,
        interactionSource = source,
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = MarkySpacing.screenHorizontal,
                vertical = MarkySpacing.rowVertical,
            ),
        ) {
            MarkyHighlightedText(
                text = title ?: stringResource(Res.string.untitled),
                highlights = if (title == null) emptyList() else titleMatches,
                style = typography.titleSmall,
                color = if (title == null) colors.inkMuted else colors.ink,
            )
            MarkyHighlightedText(
                text = snippet ?: stringResource(Res.string.no_text_yet),
                highlights = if (snippet == null) emptyList() else snippetMatches,
                style = typography.bodyMedium,
                color = if (snippet == null) colors.inkFaint else colors.inkMuted,
                modifier = Modifier.padding(top = 2.dp),
                maxLines = 2,
            )
        }
    }
}

@Composable
private fun SearchResultStates() {
    val textMatch = "…the blue tiles from Lisbon for Ana, and a cookbook for Dad."
    val textMatchStart = textMatch.indexOf("Lisbon")
    Column {
        MarkySearchResult(
            title = "Trip to Lisbon",
            snippet = "Flights are booked. Still need the tram pass and a rain jacket.",
            onClick = {},
            titleMatches = listOf(8..13),
        )
        MarkyDivider()
        MarkySearchResult(
            title = "Gift ideas",
            snippet = textMatch,
            onClick = {},
            snippetMatches = listOf(textMatchStart until textMatchStart + 6),
        )
        MarkyDivider()
        MarkySearchResult(
            title = "Gift ideas",
            snippet = textMatch,
            onClick = {},
            snippetMatches = listOf(textMatchStart until textMatchStart + 6),
            interactionSource = remember { PressedInteractionSource() },
        )
        MarkyDivider()
        MarkySearchResult(
            title = null,
            snippet = "Call the landlord about the boiler before Lisbon.",
            onClick = {},
            snippetMatches = listOf(42..47),
        )
        MarkyDivider()
        MarkySearchResult(
            title = "Lisbon",
            snippet = null,
            onClick = {},
            titleMatches = listOf(0..5),
        )
    }
}

@Preview(name = "Search result · light", widthDp = 360)
@Composable
private fun SearchResultLightPreview() {
    MarkyPreview(padding = 0.dp) { SearchResultStates() }
}

@Preview(name = "Search result · dark", widthDp = 360)
@Composable
private fun SearchResultDarkPreview() {
    MarkyPreview(darkTheme = true, padding = 0.dp) { SearchResultStates() }
}
