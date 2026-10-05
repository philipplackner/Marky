package com.plcoding.marky.feature.notes.presentation.note

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.runtime.Composable

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal actual fun isKeyboardVisible(): Boolean = WindowInsets.isImeVisible
