package com.plcoding.marky.feature.notes.presentation.note.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.plcoding.marky.core.designsystem.components.bar.MarkyTopBar
import com.plcoding.marky.core.designsystem.components.button.MarkyIconButton
import com.plcoding.marky.core.designsystem.components.menu.MarkyDropdownMenu
import com.plcoding.marky.core.designsystem.components.menu.MarkyMenuDivider
import com.plcoding.marky.core.designsystem.components.menu.MarkyMenuItem
import com.plcoding.marky.core.designsystem.components.toggle.MarkySegmentedToggle
import com.plcoding.marky.core.designsystem.icon.MarkyIcons
import com.plcoding.marky.core.designsystem.theme.MarkyTheme
import com.plcoding.marky.feature.notes.presentation.generated.resources.Res
import com.plcoding.marky.feature.notes.presentation.generated.resources.back
import com.plcoding.marky.feature.notes.presentation.generated.resources.delete_note
import com.plcoding.marky.feature.notes.presentation.generated.resources.export_md
import com.plcoding.marky.feature.notes.presentation.generated.resources.mode_read
import com.plcoding.marky.feature.notes.presentation.generated.resources.mode_write
import com.plcoding.marky.feature.notes.presentation.generated.resources.more
import com.plcoding.marky.feature.notes.presentation.generated.resources.pin_note
import com.plcoding.marky.feature.notes.presentation.generated.resources.unpin_note
import com.plcoding.marky.feature.notes.presentation.note.NoteMode
import org.jetbrains.compose.resources.stringResource

/**
 * Back, the Write / Read toggle and the overflow menu with pin, export and delete.
 */
@Composable
internal fun NoteTopBar(
    mode: NoteMode,
    isPinned: Boolean,
    isMenuOpen: Boolean,
    onBackClick: () -> Unit,
    onModeSelect: (NoteMode) -> Unit,
    onMenuClick: () -> Unit,
    onMenuDismiss: () -> Unit,
    onPinClick: () -> Unit,
    onExportClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets = WindowInsets.statusBars,
) {
    MarkyTopBar(modifier = modifier, windowInsets = windowInsets) {
        MarkyIconButton(
            icon = MarkyIcons.Back,
            contentDescription = stringResource(Res.string.back),
            onClick = onBackClick,
        )
        Spacer(modifier = Modifier.weight(1f))
        MarkySegmentedToggle(
            options = listOf(stringResource(Res.string.mode_write), stringResource(Res.string.mode_read)),
            selectedIndex = mode.ordinal,
            onSelect = { onModeSelect(NoteMode.entries[it]) },
        )
        Box {
            MarkyIconButton(
                icon = MarkyIcons.More,
                contentDescription = stringResource(Res.string.more),
                onClick = onMenuClick,
            )
            MarkyDropdownMenu(expanded = isMenuOpen, onDismissRequest = onMenuDismiss) {
                MarkyMenuItem(
                    text = stringResource(if (isPinned) Res.string.unpin_note else Res.string.pin_note),
                    icon = if (isPinned) MarkyIcons.PinOff else MarkyIcons.Pin,
                    onClick = onPinClick,
                )
                MarkyMenuItem(
                    text = stringResource(Res.string.export_md),
                    icon = MarkyIcons.Export,
                    onClick = onExportClick,
                )
                MarkyMenuDivider()
                MarkyMenuItem(
                    text = stringResource(Res.string.delete_note),
                    icon = MarkyIcons.Trash,
                    onClick = onDeleteClick,
                    isDestructive = true,
                )
            }
        }
    }
}

@Composable
private fun TopBarPreview(
    mode: NoteMode,
    darkTheme: Boolean = false,
    isMenuOpen: Boolean = false,
    isPinned: Boolean = false,
) {
    MarkyTheme(darkTheme = darkTheme) {
        Box(modifier = Modifier.background(MarkyTheme.colors.background)) {
            NoteTopBar(
                mode = mode,
                isPinned = isPinned,
                isMenuOpen = isMenuOpen,
                onBackClick = {},
                onModeSelect = {},
                onMenuClick = {},
                onMenuDismiss = {},
                onPinClick = {},
                onExportClick = {},
                onDeleteClick = {},
                windowInsets = WindowInsets(0),
            )
        }
    }
}

@Preview(name = "Read", widthDp = 360)
@Composable
private fun ReadTopBarPreview() {
    TopBarPreview(NoteMode.Read)
}

@Preview(name = "Write", widthDp = 360)
@Composable
private fun WriteTopBarPreview() {
    TopBarPreview(NoteMode.Write)
}

@Preview(name = "Write, dark", widthDp = 360)
@Composable
private fun WriteDarkTopBarPreview() {
    TopBarPreview(NoteMode.Write, darkTheme = true)
}

@Preview(name = "Menu, unpinned note", widthDp = 360, heightDp = 260)
@Composable
private fun MenuUnpinnedPreview() {
    TopBarPreview(NoteMode.Read, isMenuOpen = true)
}

@Preview(name = "Menu, pinned note", widthDp = 360, heightDp = 260)
@Composable
private fun MenuPinnedPreview() {
    TopBarPreview(NoteMode.Read, isMenuOpen = true, isPinned = true)
}

@Preview(name = "Menu, dark", widthDp = 360, heightDp = 260)
@Composable
private fun MenuDarkPreview() {
    TopBarPreview(NoteMode.Read, darkTheme = true, isMenuOpen = true)
}
