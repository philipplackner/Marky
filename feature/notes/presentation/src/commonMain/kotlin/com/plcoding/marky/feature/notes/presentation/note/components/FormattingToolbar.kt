package com.plcoding.marky.feature.notes.presentation.note.components

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.plcoding.marky.core.designsystem.components.toolbar.MarkyToolbar
import com.plcoding.marky.core.designsystem.components.toolbar.MarkyToolbarButton
import com.plcoding.marky.core.designsystem.icon.MarkyIcons
import com.plcoding.marky.core.designsystem.theme.MarkyTheme
import com.plcoding.marky.feature.notes.domain.MarkdownFormat
import com.plcoding.marky.feature.notes.presentation.generated.resources.Res
import com.plcoding.marky.feature.notes.presentation.generated.resources.format_bold
import com.plcoding.marky.feature.notes.presentation.generated.resources.format_bold_label
import com.plcoding.marky.feature.notes.presentation.generated.resources.format_checklist
import com.plcoding.marky.feature.notes.presentation.generated.resources.format_code
import com.plcoding.marky.feature.notes.presentation.generated.resources.format_heading
import com.plcoding.marky.feature.notes.presentation.generated.resources.format_heading_label
import com.plcoding.marky.feature.notes.presentation.generated.resources.format_italic
import com.plcoding.marky.feature.notes.presentation.generated.resources.format_italic_label
import com.plcoding.marky.feature.notes.presentation.generated.resources.format_link
import com.plcoding.marky.feature.notes.presentation.generated.resources.format_list
import com.plcoding.marky.feature.notes.presentation.generated.resources.format_quote
import org.jetbrains.compose.resources.stringResource

/**
 * The formatting toolbar above the keyboard in Write mode.
 *
 * @param enabled false while the title has focus.
 */
@Composable
internal fun FormattingToolbar(
    activeFormats: Set<MarkdownFormat>,
    enabled: Boolean,
    onFormatClick: (MarkdownFormat) -> Unit,
    modifier: Modifier = Modifier,
) {
    MarkyToolbar(modifier = modifier) {
        MarkyToolbarButton(
            label = stringResource(Res.string.format_heading_label),
            contentDescription = stringResource(Res.string.format_heading),
            onClick = { onFormatClick(MarkdownFormat.Heading) },
            isActive = MarkdownFormat.Heading in activeFormats,
            enabled = enabled,
        )
        MarkyToolbarButton(
            label = stringResource(Res.string.format_bold_label),
            contentDescription = stringResource(Res.string.format_bold),
            onClick = { onFormatClick(MarkdownFormat.Bold) },
            isActive = MarkdownFormat.Bold in activeFormats,
            enabled = enabled,
        )
        MarkyToolbarButton(
            label = stringResource(Res.string.format_italic_label),
            contentDescription = stringResource(Res.string.format_italic),
            onClick = { onFormatClick(MarkdownFormat.Italic) },
            isActive = MarkdownFormat.Italic in activeFormats,
            enabled = enabled,
            italic = true,
        )
        IconFormats.forEach { (format, icon, description) ->
            MarkyToolbarButton(
                icon = icon,
                contentDescription = stringResource(description),
                onClick = { onFormatClick(format) },
                isActive = format in activeFormats,
                enabled = enabled,
            )
        }
    }
}

private val IconFormats = listOf(
    Triple(MarkdownFormat.BulletList, MarkyIcons.BulletList, Res.string.format_list),
    Triple(MarkdownFormat.Checklist, MarkyIcons.Checklist, Res.string.format_checklist),
    Triple(MarkdownFormat.Quote, MarkyIcons.Quote, Res.string.format_quote),
    Triple(MarkdownFormat.Code, MarkyIcons.Code, Res.string.format_code),
    Triple(MarkdownFormat.Link, MarkyIcons.Link, Res.string.format_link),
)

@Composable
private fun ToolbarPreview(activeFormats: Set<MarkdownFormat>, enabled: Boolean, darkTheme: Boolean = false) {
    MarkyTheme(darkTheme = darkTheme) {
        FormattingToolbar(
            activeFormats = activeFormats,
            enabled = enabled,
            onFormatClick = {},
            modifier = Modifier.width(360.dp),
        )
    }
}

@Preview(name = "Enabled")
@Composable
private fun EnabledToolbarPreview() {
    ToolbarPreview(activeFormats = emptySet(), enabled = true)
}

@Preview(name = "Checklist active")
@Composable
private fun ChecklistActiveToolbarPreview() {
    ToolbarPreview(activeFormats = setOf(MarkdownFormat.Checklist), enabled = true)
}

@Preview(name = "Italic active, dark")
@Composable
private fun ItalicActiveDarkToolbarPreview() {
    ToolbarPreview(activeFormats = setOf(MarkdownFormat.Italic), enabled = true, darkTheme = true)
}

@Preview(name = "Disabled while editing the title")
@Composable
private fun DisabledToolbarPreview() {
    ToolbarPreview(activeFormats = emptySet(), enabled = false)
}
