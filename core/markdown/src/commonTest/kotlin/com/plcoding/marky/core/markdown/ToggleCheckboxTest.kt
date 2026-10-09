package com.plcoding.marky.core.markdown

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

class ToggleCheckboxTest {

    private fun offsetOf(text: String) = (parseMarkdown(text).blocks.last() as MarkdownBlock.ChecklistItem).checkboxOffset

    @Test
    fun `unchecked box becomes checked`() {
        val text = "- [ ] Sunscreen"
        assertThat(toggleCheckbox(text, offsetOf(text))).isEqualTo("- [x] Sunscreen")
    }

    @Test
    fun `checked box in either case becomes unchecked`() {
        assertThat(toggleCheckbox("- [x] a", 2)).isEqualTo("- [ ] a")
        assertThat(toggleCheckbox("- [X] a", 2)).isEqualTo("- [ ] a")
    }

    @Test
    fun `toggling twice gives the original text`() {
        val text = "🇵🇹 trip\n\n- [ ] 🎒 bag\n- [x] done"
        val offset = offsetOf("🇵🇹 trip\n\n- [ ] 🎒 bag")
        assertThat(toggleCheckbox(toggleCheckbox(text, offset), offset)).isEqualTo(text)
    }

    @Test
    fun `offset that no longer points at a checkbox leaves the text unchanged`() {
        val text = "- [ ] a"
        listOf(-1, 0, 1, 3, text.length, text.length + 5).forEach { offset ->
            assertThat(toggleCheckbox(text, offset), name = "offset=$offset").isEqualTo(text)
        }
        assertThat(toggleCheckbox("[a]", 0)).isEqualTo("[a]")
    }

    @Test
    fun `checkbox inside a quote is toggled in place`() {
        val text = "> Pack\n>\n> - [ ] Passport"
        val quoted = (parseMarkdown(text).blocks.single() as MarkdownBlock.Quote).children.last()
        val offset = (quoted as MarkdownBlock.ChecklistItem).checkboxOffset
        assertThat(toggleCheckbox(text, offset)).isEqualTo("> Pack\n>\n> - [x] Passport")
    }

    @Test
    fun `nested checkbox is toggled in place`() {
        val text = "- Pack\n  - [ ] Passport"
        val nested = (parseMarkdown(text).blocks.single() as MarkdownBlock.BulletItem).children.single()
        val offset = (nested as MarkdownBlock.ChecklistItem).checkboxOffset
        assertThat(toggleCheckbox(text, offset)).isEqualTo("- Pack\n  - [x] Passport")
    }
}
