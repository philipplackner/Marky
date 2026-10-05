package com.plcoding.marky.core.designsystem.components.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.DialogProperties

internal expect fun markyDialogProperties(scrim: Color): DialogProperties

/**
 * Call inside the dialog's content so the platform dims the screen with [scrim].
 */
@Composable
internal expect fun ApplyDialogScrim(scrim: Color)
