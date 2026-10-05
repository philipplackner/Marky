package com.plcoding.marky.core.designsystem.components.bar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.plcoding.marky.core.designsystem.components.button.MarkyIconButton
import com.plcoding.marky.core.designsystem.components.button.MarkyIconButtonStyle
import com.plcoding.marky.core.designsystem.components.field.MarkySearchEntry
import com.plcoding.marky.core.designsystem.components.field.MarkySearchField
import com.plcoding.marky.core.designsystem.components.list.MarkyDivider
import com.plcoding.marky.core.designsystem.components.text.MarkyText
import com.plcoding.marky.core.designsystem.components.toggle.MarkySegmentedToggle
import com.plcoding.marky.core.designsystem.icon.MarkyIcons
import com.plcoding.marky.core.designsystem.preview.MarkyPreview
import com.plcoding.marky.core.designsystem.theme.MarkySpacing
import com.plcoding.marky.core.designsystem.theme.MarkyTheme

/**
 * App bar without a background: a row of 52 dp min height with 10 dp gaps. Used for
 * the note screen (back, toggle, overflow), the search screen (back, search field)
 * and the top of the expanded notes list (import). Use `Modifier.weight(1f)` or a
 * [Spacer] to push items to the end.
 *
 * @param windowInsets Status bar by default; the content is drawn edge to edge.
 */
@Composable
fun MarkyTopBar(
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets = WindowInsets.statusBars,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(windowInsets)
            .heightIn(min = 52.dp)
            .padding(start = 16.dp, top = 6.dp, end = 16.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        content = content,
    )
}

/**
 * Collapsed notes-list bar: small centered title, actions on the right, divider at
 * the bottom. Shown once the list scrolls.
 *
 * @param actions Plain [MarkyIconButton]s.
 */
@Composable
fun MarkyCenterTopBar(
    title: String,
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets = WindowInsets.statusBars,
    actions: @Composable RowScope.() -> Unit = {},
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(windowInsets)
                .heightIn(min = 52.dp)
                .padding(start = 16.dp, top = 6.dp, end = 16.dp, bottom = 6.dp),
            contentAlignment = Alignment.Center,
        ) {
            MarkyText(
                text = title,
                // Keeps a long title centered and clear of up to two 40 dp actions on either side.
                modifier = Modifier.padding(horizontal = 96.dp),
                style = MarkyTheme.typography.titleSmall,
                color = MarkyTheme.colors.ink,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                modifier = Modifier.align(Alignment.CenterEnd),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                content = actions,
            )
        }
        MarkyDivider()
    }
}

/**
 * Big screen title of the expanded notes list, below [MarkyTopBar].
 */
@Composable
fun MarkyLargeTitle(
    text: String,
    modifier: Modifier = Modifier,
) {
    MarkyText(
        text = text,
        modifier = modifier
            .fillMaxWidth()
            .padding(start = MarkySpacing.screenHorizontal, top = 4.dp, end = MarkySpacing.screenHorizontal, bottom = 14.dp),
        style = MarkyTheme.typography.display,
        color = MarkyTheme.colors.ink,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun ExpandedListBar() {
    Column {
        MarkyTopBar(windowInsets = WindowInsets(0)) {
            Spacer(modifier = Modifier.weight(1f))
            MarkyIconButton(icon = MarkyIcons.Import, contentDescription = "Import", onClick = {})
        }
        MarkyLargeTitle(text = "Notes")
        MarkySearchEntry(
            placeholder = "Search notes",
            onClick = {},
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 18.dp),
        )
    }
}

@Composable
private fun CollapsedListBar() {
    MarkyCenterTopBar(title = "Notes", windowInsets = WindowInsets(0)) {
        MarkyIconButton(
            icon = MarkyIcons.Search,
            contentDescription = "Search",
            onClick = {},
            style = MarkyIconButtonStyle.Plain,
        )
        MarkyIconButton(
            icon = MarkyIcons.Import,
            contentDescription = "Import",
            onClick = {},
            style = MarkyIconButtonStyle.Plain,
        )
    }
}

@Composable
private fun NoteBar() {
    MarkyTopBar(windowInsets = WindowInsets(0)) {
        MarkyIconButton(icon = MarkyIcons.Back, contentDescription = "Back", onClick = {})
        Spacer(modifier = Modifier.weight(1f))
        MarkySegmentedToggle(options = listOf("Write", "Read"), selectedIndex = 0, onSelect = {})
        MarkyIconButton(icon = MarkyIcons.More, contentDescription = "More", onClick = {})
    }
}

@Composable
private fun SearchBar() {
    MarkyTopBar(windowInsets = WindowInsets(0)) {
        MarkyIconButton(icon = MarkyIcons.Back, contentDescription = "Back", onClick = {})
        MarkySearchField(
            query = "lisbon",
            onQueryChange = {},
            placeholder = "Search notes",
            onClearClick = {},
            clearContentDescription = "Clear search",
            modifier = Modifier.weight(1f),
        )
    }
}

@Preview(name = "Notes list · expanded · light", widthDp = 360)
@Composable
private fun ExpandedListLightPreview() {
    MarkyPreview(padding = 0.dp) { ExpandedListBar() }
}

@Preview(name = "Notes list · expanded · dark", widthDp = 360)
@Composable
private fun ExpandedListDarkPreview() {
    MarkyPreview(darkTheme = true, padding = 0.dp) { ExpandedListBar() }
}

@Preview(name = "Notes list · collapsed · light", widthDp = 360)
@Composable
private fun CollapsedListLightPreview() {
    MarkyPreview(padding = 0.dp) { CollapsedListBar() }
}

@Preview(name = "Notes list · collapsed · dark", widthDp = 360)
@Composable
private fun CollapsedListDarkPreview() {
    MarkyPreview(darkTheme = true, padding = 0.dp) { CollapsedListBar() }
}

@Preview(name = "Note · light", widthDp = 360)
@Composable
private fun NoteBarLightPreview() {
    MarkyPreview(padding = 0.dp) { NoteBar() }
}

@Preview(name = "Note · dark", widthDp = 360)
@Composable
private fun NoteBarDarkPreview() {
    MarkyPreview(darkTheme = true, padding = 0.dp) { NoteBar() }
}

@Preview(name = "Search · light", widthDp = 360)
@Composable
private fun SearchBarLightPreview() {
    MarkyPreview(padding = 0.dp) { SearchBar() }
}

@Preview(name = "Search · dark", widthDp = 360)
@Composable
private fun SearchBarDarkPreview() {
    MarkyPreview(darkTheme = true, padding = 0.dp) { SearchBar() }
}
