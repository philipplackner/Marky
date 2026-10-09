package com.plcoding.marky.core.markdown

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.length
import kotlin.test.Test

class PlainPreviewTest {

    @Test
    fun `syntax is removed and lines are joined with single spaces`() {
        val text = "## Packing\n\nFlights are **booked**. Still need the *tram pass*.\n\n- [x] Passport\n> Go `early`\n\n[Tram 28](https://carris.pt)"
        assertThat(text.toPlainPreview())
            .isEqualTo("Packing Flights are booked. Still need the tram pass. Passport Go early Tram 28")
    }

    @Test
    fun `nested list items appear after their parent`() {
        assertThat("- Food\n  - [ ] Nata\n    1. Belém\n- Sights".toPlainPreview())
            .isEqualTo("Food Nata Belém Sights")
    }

    @Test
    fun `blocks inside a quote are joined like top-level blocks`() {
        assertThat("> Go **early**\n>\n> - [ ] Nata\n> ## Belém".toPlainPreview()).isEqualTo("Go early Nata Belém")
    }

    @Test
    fun `empty or syntax only text gives an empty preview`() {
        assertThat("".toPlainPreview()).isEmpty()
        assertThat("  \n\n\t ".toPlainPreview()).isEmpty()
        assertThat("#\n\n- [ ] \n\n```\n```".toPlainPreview()).isEmpty()
    }

    @Test
    fun `emoji and other scripts survive`() {
        assertThat("**Привет** 👋🏽\n日本語".toPlainPreview()).isEqualTo("Привет 👋🏽 日本語")
    }

    @Test
    fun `long text is cut to the max length`() {
        assertThat("word ".repeat(1_000).toPlainPreview(maxLength = 50)).length().isEqualTo(50)
    }

    @Test
    fun `plain text keeps the whole note`() {
        val text = "word ".repeat(1_000) + "**Lisbon**"
        val plain = text.toPlainText()
        assertThat(plain.length).isEqualTo(5_000 + "Lisbon".length)
        assertThat(plain.takeLast(6)).isEqualTo("Lisbon")
    }

    @Test
    fun `plain text strips syntax like the preview`() {
        val text = "## Packing\n\n- [x] **Passport**\n\n[Tram 28](https://carris.pt)"
        assertThat(text.toPlainText()).isEqualTo("Packing Passport Tram 28")
    }
}
