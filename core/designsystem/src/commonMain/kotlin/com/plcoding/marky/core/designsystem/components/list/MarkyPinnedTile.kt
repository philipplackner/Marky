package com.plcoding.marky.core.designsystem.components.list

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plcoding.marky.core.designsystem.components.icon.MarkyIcon
import com.plcoding.marky.core.designsystem.components.text.MarkyText
import com.plcoding.marky.core.designsystem.generated.resources.Res
import com.plcoding.marky.core.designsystem.generated.resources.untitled
import com.plcoding.marky.core.designsystem.icon.MarkyIcons
import com.plcoding.marky.core.designsystem.preview.MarkyPreview
import com.plcoding.marky.core.designsystem.preview.PressedInteractionSource
import com.plcoding.marky.core.designsystem.theme.MarkyShapes
import com.plcoding.marky.core.designsystem.theme.MarkyTheme
import org.jetbrains.compose.resources.stringResource

/**
 * Yellow tile for a pinned note: title and preview up to two lines each, time at
 * the bottom, pin mark top right. Tiles sit in a horizontally scrolling row with
 * 10 dp gaps.
 *
 * @param title `null` shows "Untitled", faded.
 * @param preview Plain-text preview; left out when blank.
 */
@Composable
fun MarkyPinnedTile(
    title: String?,
    preview: String,
    timestamp: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource? = null,
) {
    val colors = MarkyTheme.colors
    val typography = MarkyTheme.typography
    val fonts = MarkyTheme.fonts
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val isPressed by source.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = tween(durationMillis = 120),
    )
    Surface(
        onClick = onClick,
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .width(136.dp)
            .heightIn(min = 116.dp),
        shape = MarkyShapes.extraLarge,
        color = if (isPressed) colors.highlightPressed else colors.highlight,
        contentColor = colors.onHighlight,
        interactionSource = source,
    ) {
        Box {
            Column(
                modifier = Modifier
                    .heightIn(min = 116.dp)
                    .padding(14.dp),
            ) {
                MarkyText(
                    text = title ?: stringResource(Res.string.untitled),
                    modifier = Modifier
                        .padding(end = 18.dp)
                        .alpha(if (title == null) 0.6f else 1f),
                    style = typography.titleSmall.copy(
                        fontFamily = fonts.bricolage(16.sp),
                        fontSize = 16.sp,
                        lineHeight = 20.sp,
                    ),
                    color = colors.onHighlight,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                if (preview.isNotBlank()) {
                    MarkyText(
                        text = preview,
                        modifier = Modifier
                            .padding(top = 6.dp)
                            .alpha(0.72f),
                        style = typography.bodyMedium.copy(fontSize = 12.5.sp, lineHeight = 17.sp),
                        color = colors.onHighlight,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                MarkyText(
                    text = timestamp,
                    modifier = Modifier.padding(top = 8.dp),
                    style = typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = colors.onHighlight,
                    maxLines = 1,
                )
            }
            MarkyIcon(
                imageVector = MarkyIcons.Pin,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 13.dp, end = 12.dp)
                    .alpha(0.65f),
                tint = colors.onHighlight,
                size = 16.dp,
                strokeWidth = 2f,
            )
        }
    }
}

@Composable
private fun PinnedTileStates() {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        MarkyPinnedTile(
            title = "Reading list",
            preview = "The Overstory, Klara and the Sun, Dune",
            timestamp = "3 d",
            onClick = {},
        )
        MarkyPinnedTile(
            title = "Reading list",
            preview = "The Overstory, Klara and the Sun, Dune",
            timestamp = "3 d",
            onClick = {},
            interactionSource = remember { PressedInteractionSource() },
        )
        MarkyPinnedTile(
            title = null,
            preview = "Door code 4471, bins go out on Tuesday",
            timestamp = "1 w",
            onClick = {},
        )
        MarkyPinnedTile(
            title = "Questions for the next team offsite",
            preview = "Who owns onboarding?",
            timestamp = "Just now",
            onClick = {},
        )
    }
}

@Preview(name = "Pinned tile · light", widthDp = 600)
@Composable
private fun PinnedTileLightPreview() {
    MarkyPreview { PinnedTileStates() }
}

@Preview(name = "Pinned tile · dark", widthDp = 600)
@Composable
private fun PinnedTileDarkPreview() {
    MarkyPreview(darkTheme = true) { PinnedTileStates() }
}
