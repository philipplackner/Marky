package com.plcoding.marky.core.designsystem.components.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.plcoding.marky.core.designsystem.components.text.MarkyText
import com.plcoding.marky.core.designsystem.preview.MarkyPreview
import com.plcoding.marky.core.designsystem.theme.MarkySpacing
import com.plcoding.marky.core.designsystem.theme.MarkyTheme

/**
 * Groups content on the list and search screens. Sentence case, optional count on the right.
 */
@Composable
fun MarkySectionLabel(
    text: String,
    modifier: Modifier = Modifier,
    count: Int? = null,
    contentPadding: PaddingValues = PaddingValues(
        start = MarkySpacing.screenHorizontal,
        end = MarkySpacing.screenHorizontal,
        bottom = 10.dp,
    ),
) {
    val style = MarkyTheme.typography.labelMedium
    val color = MarkyTheme.colors.inkMuted
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(contentPadding),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        MarkyText(text = text, style = style, color = color)
        if (count != null) {
            MarkyText(text = count.toString(), style = style, color = color)
        }
    }
}

@Composable
private fun SectionLabelStates() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        MarkySectionLabel(text = "Pinned")
        MarkySectionLabel(text = "In note text", count = 3)
    }
}

@Preview(name = "Section label · light", widthDp = 360)
@Composable
private fun SectionLabelLightPreview() {
    MarkyPreview(padding = 0.dp) { SectionLabelStates() }
}

@Preview(name = "Section label · dark", widthDp = 360)
@Composable
private fun SectionLabelDarkPreview() {
    MarkyPreview(darkTheme = true, padding = 0.dp) { SectionLabelStates() }
}
