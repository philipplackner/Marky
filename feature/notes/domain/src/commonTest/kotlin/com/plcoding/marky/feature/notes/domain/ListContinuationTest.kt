package com.plcoding.marky.feature.notes.domain

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

class ListContinuationTest {

    /** Types Return at the `|` of [marked] and returns the result with its cursor as `|`. */
    private fun pressReturn(marked: String): String {
        val cursor = marked.indexOf('|')
        val text = marked.removeRange(cursor, cursor + 1)
        val old = EditorText(text, cursor)
        val new = EditorText(text.substring(0, cursor) + "\n" + text.substring(cursor), cursor + 1)
        val result = continueListOnNewline(old, new)
        return result.text.substring(0, result.selectionStart) + "|" + result.text.substring(result.selectionStart)
    }

    @Test
    fun `checklist continues with an unchecked box`() {
        assertThat(pressReturn("- [x] Passport|")).isEqualTo("- [x] Passport\n- [ ] |")
    }

    @Test
    fun `bullet numbered and quote continue with their marker`() {
        assertThat(pressReturn("* a|")).isEqualTo("* a\n* |")
        assertThat(pressReturn("9. a|")).isEqualTo("9. a\n10. |")
        assertThat(pressReturn("1) a|")).isEqualTo("1) a\n2) |")
        assertThat(pressReturn("> a|")).isEqualTo("> a\n> |")
    }

    @Test
    fun `indentation is kept`() {
        assertThat(pressReturn("  - a|")).isEqualTo("  - a\n  - |")
    }

    @Test
    fun `return in the middle of an item moves the rest to a new item`() {
        assertThat(pressReturn("- ab|cd")).isEqualTo("- ab\n- |cd")
    }

    @Test
    fun `return on an empty item ends the list`() {
        assertThat(pressReturn("- a\n- [ ] |")).isEqualTo("- a\n|")
    }

    @Test
    fun `plain lines headings and cursor inside the marker are left alone`() {
        assertThat(pressReturn("text|")).isEqualTo("text\n|")
        assertThat(pressReturn("# Title|")).isEqualTo("# Title\n|")
        assertThat(pressReturn("-| a")).isEqualTo("-\n| a")
    }

    @Test
    fun `changes other than one typed line break pass through`() {
        val old = EditorText("- a", 3)
        val pasted = EditorText("- a\nb\nc", 7)
        assertThat(continueListOnNewline(old, pasted)).isEqualTo(pasted)
        val typed = EditorText("- ab", 4)
        assertThat(continueListOnNewline(old, typed)).isEqualTo(typed)
    }
}
