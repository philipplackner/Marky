package com.plcoding.marky.core.designsystem.components.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.DialogProperties

internal actual fun markyDialogProperties(scrim: Color): DialogProperties = DialogProperties(scrimColor = scrim)

@Composable
internal actual fun ApplyDialogScrim(scrim: Color) = Unit
