package com.plcoding.marky.core.designsystem.components.empty

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.plcoding.marky.core.designsystem.components.button.MarkyButton
import com.plcoding.marky.core.designsystem.components.button.MarkyButtonSize
import com.plcoding.marky.core.designsystem.components.button.MarkyButtonStyle
import com.plcoding.marky.core.designsystem.components.text.MarkyText
import com.plcoding.marky.core.designsystem.icon.MarkyIcons
import com.plcoding.marky.core.designsystem.icon.MarkyIllustrations
import com.plcoding.marky.core.designsystem.preview.MarkyPreview
import com.plcoding.marky.core.designsystem.theme.MarkyTheme

/**
 * Line illustration, a heading, one or two sentences that say what to do, and up
 * to two buttons. The screen centers it in the free space.
 *
 * @param illustration One of the [MarkyIllustrations].
 * @param actions Up to two [MarkyButton]s, stacked and centered.
 */
@Composable
fun MarkyEmptyState(
    illustration: ImageVector,
    title: String,
    text: String,
    modifier: Modifier = Modifier,
    actions: (@Composable ColumnScope.() -> Unit)? = null,
) {
    val colors = MarkyTheme.colors
    val typography = MarkyTheme.typography
    Column(
        modifier = modifier.padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            imageVector = illustration,
            contentDescription = null,
            modifier = Modifier
                .size(width = 120.dp, height = 96.dp),
        )
        MarkyText(
            text = title,
            modifier = Modifier.padding(top = 20.dp),
            style = typography.titleMedium,
            color = colors.ink,
            textAlign = TextAlign.Center,
        )
        MarkyText(
            text = text,
            modifier = Modifier
                .widthIn(max = 252.dp)
                .padding(top = 8.dp, bottom = if (actions != null) 22.dp else 0.dp),
            style = typography.bodyMedium,
            color = colors.inkMuted,
            textAlign = TextAlign.Center,
        )
        if (actions != null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                content = actions,
            )
        }
    }
}

@Composable
private fun NoNotesState() {
    MarkyEmptyState(
        illustration = MarkyIllustrations.NoNotes,
        title = "No notes yet",
        text = "Write your first note, or bring one in from another app as a .md file.",
        modifier = Modifier.width(360.dp),
    ) {
        MarkyButton(text = "New note", onClick = {}, icon = MarkyIcons.Plus)
        MarkyButton(
            text = "Import .md file",
            onClick = {},
            style = MarkyButtonStyle.Text,
            icon = MarkyIcons.Import,
        )
    }
}

@Composable
private fun EmptyNoteState() {
    MarkyEmptyState(
        illustration = MarkyIllustrations.EmptyNote,
        title = "Nothing to read yet",
        text = "This note has no text. Switch to Write to add some.",
        modifier = Modifier.width(360.dp),
    ) {
        MarkyButton(text = "Start writing", onClick = {}, size = MarkyButtonSize.Small)
    }
}

@Composable
private fun SearchStartState() {
    MarkyEmptyState(
        illustration = MarkyIllustrations.SearchStart,
        title = "Search your notes",
        text = "Type a word from any title or note. Notes with a matching title show first.",
        modifier = Modifier.width(360.dp),
    )
}

@Preview(name = "Empty state · two buttons · light")
@Composable
private fun EmptyTwoButtonsLightPreview() {
    MarkyPreview { NoNotesState() }
}

@Preview(name = "Empty state · two buttons · dark")
@Composable
private fun EmptyTwoButtonsDarkPreview() {
    MarkyPreview(darkTheme = true) { NoNotesState() }
}

@Preview(name = "Empty state · one button · light")
@Composable
private fun EmptyOneButtonLightPreview() {
    MarkyPreview { EmptyNoteState() }
}

@Preview(name = "Empty state · one button · dark")
@Composable
private fun EmptyOneButtonDarkPreview() {
    MarkyPreview(darkTheme = true) { EmptyNoteState() }
}

@Preview(name = "Empty state · no buttons · light")
@Composable
private fun EmptyNoButtonsLightPreview() {
    MarkyPreview { SearchStartState() }
}

@Preview(name = "Empty state · no buttons · dark")
@Composable
private fun EmptyNoButtonsDarkPreview() {
    MarkyPreview(darkTheme = true) { SearchStartState() }
}
