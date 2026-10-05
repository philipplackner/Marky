package com.plcoding.marky.feature.notes.presentation.note

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.plcoding.marky.feature.notes.domain.EditorText

internal fun TextFieldValue.toEditorText(): EditorText =
    EditorText(text = text, selectionStart = selection.min, selectionEnd = selection.max)

internal fun EditorText.toTextFieldValue(): TextFieldValue =
    TextFieldValue(text = text, selection = TextRange(selectionStart, selectionEnd))
