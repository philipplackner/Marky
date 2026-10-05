package com.plcoding.marky.feature.notes.presentation.note

import androidx.compose.runtime.Stable
import androidx.compose.ui.text.input.TextFieldValue
import com.plcoding.marky.core.markdown.MarkdownDocument
import com.plcoding.marky.core.markdown.SyntaxSpan
import com.plcoding.marky.feature.notes.domain.MarkdownFormat

enum class NoteMode {
    Write,
    Read,
}

/**
 * @property document The parsed body, kept up to date while in Read mode.
 * @property syntaxSpans Write-mode styling of the body. Computed in the background, so
 * it can briefly lag one edit behind [body].
 * @property activeFormats Toolbar buttons to show as active for the current selection.
 * @property isNewNote The note was created on this screen, so it opens in Write mode with the keyboard up.
 */
@Stable
data class NoteState(
    val title: String = "",
    val body: TextFieldValue = TextFieldValue(),
    val mode: NoteMode = NoteMode.Read,
    val isPinned: Boolean = false,
    val isLoading: Boolean = true,
    val isNewNote: Boolean = false,
    val document: MarkdownDocument = MarkdownDocument(emptyList()),
    val syntaxSpans: List<SyntaxSpan> = emptyList(),
    val activeFormats: Set<MarkdownFormat> = emptySet(),
    val isMenuOpen: Boolean = false,
    val isDeleteDialogVisible: Boolean = false,
)
