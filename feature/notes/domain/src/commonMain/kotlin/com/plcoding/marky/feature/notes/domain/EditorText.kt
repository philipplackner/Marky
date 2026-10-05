package com.plcoding.marky.feature.notes.domain

/**
 * The note text with its selection. A collapsed selection is the cursor.
 *
 * @property selectionStart Never greater than [selectionEnd].
 */
data class EditorText(
    val text: String,
    val selectionStart: Int,
    val selectionEnd: Int = selectionStart,
) {
    init {
        require(selectionStart in 0..selectionEnd && selectionEnd <= text.length) {
            "Invalid selection $selectionStart..$selectionEnd for text of length ${text.length}"
        }
    }

    val hasSelection: Boolean
        get() = selectionStart != selectionEnd
}

/**
 * The eight buttons of the formatting toolbar, in toolbar order.
 */
enum class MarkdownFormat {
    Heading,
    Bold,
    Italic,
    BulletList,
    Checklist,
    Quote,
    Code,
    Link,
}
