package com.plcoding.marky.core.designsystem.components.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider

internal actual fun markyDialogProperties(scrim: Color): DialogProperties = DialogProperties()

// Android dims dialogs with black at a fixed amount; the closest match is using the token's alpha.
@Composable
internal actual fun ApplyDialogScrim(scrim: Color) {
    val window = (LocalView.current.parent as? DialogWindowProvider)?.window
    SideEffect {
        window?.setDimAmount(scrim.alpha)
    }
}
