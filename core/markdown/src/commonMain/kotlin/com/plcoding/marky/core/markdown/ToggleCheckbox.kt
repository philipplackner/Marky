package com.plcoding.marky.core.markdown

/**
 * Ticks or unticks the checklist box whose `[` is at [checkboxOffset] by writing
 * `[x]` or `[ ]` back into [text].
 *
 * @return the new text, or [text] unchanged if there is no checkbox at that offset
 * (for example because the text changed since it was parsed).
 */
fun toggleCheckbox(text: String, checkboxOffset: Int): String {
    val markIndex = checkboxOffset + 1
    val isCheckbox = checkboxOffset >= 0 &&
        checkboxOffset + 2 < text.length &&
        text[checkboxOffset] == '[' &&
        text[checkboxOffset + 2] == ']' &&
        text[markIndex] in CheckboxMarks
    if (!isCheckbox) return text
    val newMark = if (text[markIndex] == ' ') 'x' else ' '
    return text.substring(0, markIndex) + newMark + text.substring(markIndex + 1)
}

private val CheckboxMarks = setOf(' ', 'x', 'X')
