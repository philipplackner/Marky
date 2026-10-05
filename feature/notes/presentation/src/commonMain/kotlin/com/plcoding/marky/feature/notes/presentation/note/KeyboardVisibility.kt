package com.plcoding.marky.feature.notes.presentation.note

import androidx.compose.runtime.Composable

/**
 * Whether the software keyboard is shown, including floating keyboards that don't
 * take space at the bottom of the screen.
 */
@Composable
internal expect fun isKeyboardVisible(): Boolean
