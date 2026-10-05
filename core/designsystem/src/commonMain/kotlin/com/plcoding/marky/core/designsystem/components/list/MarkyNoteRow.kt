package com.plcoding.marky.core.designsystem.components.list

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plcoding.marky.core.designsystem.components.text.MarkyText
import com.plcoding.marky.core.designsystem.generated.resources.Res
import com.plcoding.marky.core.designsystem.generated.resources.no_text_yet
import com.plcoding.marky.core.designsystem.generated.resources.untitled
import com.plcoding.marky.core.designsystem.preview.MarkyPreview
import com.plcoding.marky.core.designsystem.preview.PressedInteractionSource
import com.plcoding.marky.core.designsystem.theme.MarkySpacing
import com.plcoding.marky.core.designsystem.theme.MarkyTheme
import org.jetbrains.compose.resources.stringResource

/**
 * Row for an unpinned note: one-line title, time since the last edit, and a
 * one-line preview. Put a [MarkyDivider] between rows.
 *
 * @param title `null` shows "Untitled" in muted.
 * @param preview Plain-text preview; `null` shows "No text yet".
 * @param isHighlighted Soft yellow background for a just-imported note. Set it back
 * to `false` after 2 seconds; the background fades out.
 */
@Composable
fun MarkyNoteRow(
    title: String?,
    timestamp: String,
    preview: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isHighlighted: Boolean = false,
    interactionSource: MutableInteractionSource? = null,
) {
    val colors = MarkyTheme.colors
    val typography = MarkyTheme.typography
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val isPressed by source.collectIsPressedAsState()
    val background by animateColorAsState(
        targetValue = when {
            isPressed -> colors.surface
            isHighlighted -> colors.highlightSoft
            else -> Color.Transparent
        },
        animationSpec = tween(durationMillis = 120),
    )
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        color = background,
        contentColor = colors.ink,
        interactionSource = source,
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = MarkySpacing.screenHorizontal,
                vertical = MarkySpacing.rowVertical,
            ),
        ) {
            Row(verticalAlignment = Alignment.Top) {
                MarkyText(
                    text = title ?: stringResource(Res.string.untitled),
                    modifier = Modifier.weight(1f),
                    style = typography.titleSmall,
                    color = if (title == null) colors.inkMuted else colors.ink,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.width(12.dp))
                MarkyText(
                    text = timestamp,
                    style = typography.labelSmall.copy(lineHeight = 22.sp),
                    color = colors.inkMuted,
                    maxLines = 1,
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            MarkyText(
                text = preview ?: stringResource(Res.string.no_text_yet),
                style = typography.bodyMedium,
                color = if (preview == null) colors.inkFaint else colors.inkMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun NoteRowStates() {
    Column {
        MarkyNoteRow(
            title = "Trip to Lisbon",
            timestamp = "4 min",
            preview = "Flights are booked. Still need the tram pass and a rain jacket.",
            onClick = {},
        )
        MarkyDivider()
        MarkyNoteRow(
            title = "Trip to Lisbon",
            timestamp = "4 min",
            preview = "Flights are booked. Still need the tram pass and a rain jacket.",
            onClick = {},
            interactionSource = remember { PressedInteractionSource() },
        )
        MarkyDivider()
        MarkyNoteRow(
            title = null,
            timestamp = "Yesterday",
            preview = "Call the landlord about the boiler before Friday.",
            onClick = {},
        )
        MarkyDivider()
        MarkyNoteRow(title = "Empty note", timestamp = "2 d", preview = null, onClick = {})
        MarkyDivider()
        MarkyNoteRow(
            title = "Ideas",
            timestamp = "Just now",
            preview = "Half-baked app ideas, collected on the train.",
            onClick = {},
            isHighlighted = true,
        )
        MarkyDivider(visible = false)
        MarkyNoteRow(
            title = "A very long note title that does not fit on a single line",
            timestamp = "12 Aug 2025",
            preview = "Previews are cut to one line, no matter how long the text is.",
            onClick = {},
        )
    }
}

@Preview(name = "Note row · light", widthDp = 360)
@Composable
private fun NoteRowLightPreview() {
    MarkyPreview(padding = 0.dp) { NoteRowStates() }
}

@Preview(name = "Note row · dark", widthDp = 360)
@Composable
private fun NoteRowDarkPreview() {
    MarkyPreview(darkTheme = true, padding = 0.dp) { NoteRowStates() }
}
