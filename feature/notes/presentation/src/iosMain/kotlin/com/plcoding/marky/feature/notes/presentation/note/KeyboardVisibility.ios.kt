package com.plcoding.marky.feature.notes.presentation.note

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity

@Composable
internal actual fun isKeyboardVisible(): Boolean = WindowInsets.ime.getBottom(LocalDensity.current) > 0
