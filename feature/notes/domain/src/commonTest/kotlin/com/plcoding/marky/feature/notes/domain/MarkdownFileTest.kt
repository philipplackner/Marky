package com.plcoding.marky.feature.notes.domain

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import kotlin.test.Test

class MarkdownFileTest {

    @Test
    fun `the title is the file name without the md extension and keeps its case`() {
        assertThat(noteTitleFromFileName("ideas.md")).isEqualTo("ideas")
        assertThat(noteTitleFromFileName("Trip to Lisbon.md")).isEqualTo("Trip to Lisbon")
    }

    @Test
    fun `the extension is matched without case`() {
        assertThat(noteTitleFromFileName("Ideas.MD")).isEqualTo("Ideas")
    }

    @Test
    fun `only one md extension is removed`() {
        assertThat(noteTitleFromFileName("notes.md.md")).isEqualTo("notes.md")
    }

    @Test
    fun `other dots stay in the title`() {
        assertThat(noteTitleFromFileName("v1.2 release.md")).isEqualTo("v1.2 release")
        assertThat(noteTitleFromFileName("readme")).isEqualTo("readme")
    }

    @Test
    fun `a file called only md gives an empty title`() {
        assertThat(noteTitleFromFileName(".md")).isEqualTo("")
        assertThat(noteTitleFromFileName("  .md")).isEqualTo("")
    }

    @Test
    fun `emoji and other scripts in file names survive`() {
        assertThat(noteTitleFromFileName("Привет 👋🏽.md")).isEqualTo("Привет 👋🏽")
    }

    @Test
    fun `utf8 text is decoded and a byte order mark is dropped`() {
        val text = "# Café 👍🏽\n- [ ] Nata"
        assertThat(decodeMarkdownFile(text.encodeToByteArray())).isEqualTo(text)
        assertThat(decodeMarkdownFile(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()) + text.encodeToByteArray()))
            .isEqualTo(text)
    }

    @Test
    fun `an empty file is an empty note`() {
        assertThat(decodeMarkdownFile(ByteArray(0))).isEqualTo("")
    }

    @Test
    fun `bytes that aren't utf8 are rejected`() {
        val pngHeader = byteArrayOf(0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A)
        assertThat(decodeMarkdownFile(pngHeader)).isNull()
        assertThat(decodeMarkdownFile(byteArrayOf(0xE2.toByte(), 0x82.toByte()))).isNull()
    }

    @Test
    fun `valid utf8 with nul bytes counts as binary`() {
        assertThat(decodeMarkdownFile(byteArrayOf(0x50, 0x4B, 0x03, 0x04, 0x00, 0x00))).isNull()
    }

    @Test
    fun `exported text round trips through import`() {
        val text = "## Packing\n\nPassport, sunscreen 🌞"
        assertThat(decodeMarkdownFile(encodeMarkdownFile(text))).isEqualTo(text)
    }

    @Test
    fun `the export file name is the title with md`() {
        assertThat(exportFileName("Trip to Lisbon")).isEqualTo("Trip to Lisbon.md")
        assertThat(exportFileName("  Ideas ")).isEqualTo("Ideas.md")
    }

    @Test
    fun `a note without a title has no export file name`() {
        assertThat(exportFileName("")).isNull()
        assertThat(exportFileName(" \n ")).isNull()
    }
}
