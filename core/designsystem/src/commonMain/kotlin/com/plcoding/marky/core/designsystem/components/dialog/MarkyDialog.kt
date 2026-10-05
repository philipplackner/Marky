package com.plcoding.marky.core.designsystem.components.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.plcoding.marky.core.designsystem.components.button.MarkyButton
import com.plcoding.marky.core.designsystem.components.button.MarkyButtonStyle
import com.plcoding.marky.core.designsystem.components.text.MarkyText
import com.plcoding.marky.core.designsystem.preview.MarkyPreview
import com.plcoding.marky.core.designsystem.theme.MarkyShapes
import com.plcoding.marky.core.designsystem.theme.MarkyTheme
import com.plcoding.marky.core.designsystem.theme.markyShadowLarge

/**
 * Confirmation dialog for destructive actions, like deleting a note. The two buttons
 * share the width; the destructive one is on the right.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarkyDialog(
    title: String,
    text: String,
    dismissLabel: String,
    confirmLabel: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrim = MarkyTheme.colors.scrim
    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        properties = markyDialogProperties(scrim),
    ) {
        ApplyDialogScrim(scrim)
        DialogCard(
            title = title,
            text = text,
            dismissLabel = dismissLabel,
            confirmLabel = confirmLabel,
            onDismiss = onDismiss,
            onConfirm = onConfirm,
        )
    }
}

@Composable
private fun DialogCard(
    title: String,
    text: String,
    dismissLabel: String,
    confirmLabel: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    val colors = MarkyTheme.colors
    val typography = MarkyTheme.typography
    Surface(
        modifier = Modifier
            .widthIn(max = 300.dp)
            .fillMaxWidth()
            .markyShadowLarge(MarkyShapes.extraExtraLarge, colors.shadowLarge),
        shape = MarkyShapes.extraExtraLarge,
        color = colors.raised,
        contentColor = colors.ink,
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            MarkyText(text = title, style = typography.titleMedium, color = colors.ink)
            MarkyText(
                text = text,
                modifier = Modifier.padding(top = 8.dp, bottom = 22.dp),
                style = typography.bodyMedium,
                color = colors.inkMuted,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MarkyButton(
                    text = dismissLabel,
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    style = MarkyButtonStyle.Secondary,
                )
                MarkyButton(
                    text = confirmLabel,
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f),
                    style = MarkyButtonStyle.Danger,
                )
            }
        }
    }
}

@Composable
private fun DialogPreviewContent() {
    DialogCard(
        title = "Delete \"Trip to Lisbon\"?",
        text = "The note will be removed from this device. You can't undo this.",
        dismissLabel = "Cancel",
        confirmLabel = "Delete",
        onDismiss = {},
        onConfirm = {},
    )
}

@Preview(name = "Dialog · light")
@Composable
private fun DialogLightPreview() {
    MarkyPreview(padding = 24.dp) { DialogPreviewContent() }
}

@Preview(name = "Dialog · dark")
@Composable
private fun DialogDarkPreview() {
    MarkyPreview(darkTheme = true, padding = 24.dp) { DialogPreviewContent() }
}
