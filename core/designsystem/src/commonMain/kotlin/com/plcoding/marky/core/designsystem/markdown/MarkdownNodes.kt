package com.plcoding.marky.core.designsystem.markdown

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plcoding.marky.core.designsystem.components.checkbox.MarkyCheckbox
import com.plcoding.marky.core.designsystem.components.text.MarkyText
import com.plcoding.marky.core.designsystem.theme.MarkyShapes
import com.plcoding.marky.core.designsystem.theme.MarkyTheme

/**
 * `#`, `##` or `###` heading in Read mode. Level 2 gets the yellow marker swash.
 *
 * @param level 1, 2 or 3.
 */
@Composable
fun MarkdownHeading(
    text: AnnotatedString,
    level: Int,
    modifier: Modifier = Modifier,
) {
    require(level in 1..3) { "Heading level must be 1, 2 or 3, was $level" }
    val typography = MarkyTheme.typography
    MarkdownText(
        text = text,
        style = when (level) {
            1 -> typography.heading1
            2 -> typography.heading2
            else -> typography.heading3
        },
        color = MarkyTheme.colors.ink,
        modifier = modifier,
        swash = level == 2,
    )
}

/**
 * Paragraph of note text with inline formatting.
 */
@Composable
fun MarkdownParagraph(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
) {
    MarkdownText(
        text = text,
        style = MarkyTheme.typography.bodyLarge,
        color = MarkyTheme.colors.ink,
        modifier = modifier,
    )
}

/**
 * One `- ` list item with a round bullet.
 */
@Composable
fun MarkdownBulletItem(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
) {
    val ink = MarkyTheme.colors.ink
    Box(modifier = modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .offset(x = 6.dp, y = 10.dp)
                .size(6.dp),
        ) {
            drawCircle(color = ink)
        }
        MarkdownParagraph(text = text, modifier = Modifier.padding(start = 22.dp))
    }
}

/**
 * One `1. ` list item.
 */
@Composable
fun MarkdownNumberedItem(
    number: Int,
    text: AnnotatedString,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxWidth()) {
        MarkyText(
            text = "$number.",
            modifier = Modifier.offset(x = 2.dp),
            style = MarkyTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            color = MarkyTheme.colors.inkMuted,
            maxLines = 1,
        )
        MarkdownParagraph(text = text, modifier = Modifier.padding(start = 26.dp))
    }
}

/**
 * One `- [ ] ` / `- [x] ` item. Ticking it should write `[x]` back to the Markdown.
 * Checked items turn muted but are not struck through.
 */
@Composable
fun MarkdownChecklistItem(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    text: AnnotatedString,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // The circle takes 22 dp in the layout while its 44 dp touch area overlaps the surroundings.
        Box(
            modifier = Modifier
                .size(22.dp)
                .wrapContentSize(unbounded = true),
        ) {
            MarkyCheckbox(checked = checked, onCheckedChange = onCheckedChange)
        }
        Spacer(modifier = Modifier.width(12.dp))
        MarkdownText(
            text = text,
            style = MarkyTheme.typography.bodyLarge,
            color = if (checked) MarkyTheme.colors.inkMuted else MarkyTheme.colors.ink,
        )
    }
}

/**
 * `> ` quote: soft surface card with a big quote mark.
 */
@Composable
fun MarkdownQuote(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
) {
    val colors = MarkyTheme.colors
    val fonts = MarkyTheme.fonts
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MarkyShapes.large,
        color = colors.surface,
        contentColor = colors.ink,
    ) {
        Box {
            MarkyText(
                text = "“",
                modifier = Modifier.offset(x = 12.dp, y = 4.dp),
                style = MarkyTheme.typography.heading1.copy(
                    fontFamily = fonts.bricolage(44.sp),
                    fontSize = 44.sp,
                    lineHeight = 44.sp,
                    letterSpacing = 0.sp,
                ),
                color = colors.quoteMark,
            )
            MarkdownText(
                text = text,
                style = MarkyTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = colors.ink,
                modifier = Modifier.padding(start = 44.dp, top = 14.dp, end = 16.dp, bottom = 14.dp),
            )
        }
    }
}

/**
 * Fenced code block. Lines don't wrap; long lines are cut off at the edge.
 */
@Composable
fun MarkdownCodeBlock(
    code: String,
    modifier: Modifier = Modifier,
) {
    val colors = MarkyTheme.colors
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MarkyShapes.large,
        color = colors.codeBackground,
        contentColor = colors.codeInk,
    ) {
        MarkyText(
            text = code,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            style = MarkyTheme.typography.code,
            color = colors.codeInk,
            softWrap = false,
        )
    }
}
