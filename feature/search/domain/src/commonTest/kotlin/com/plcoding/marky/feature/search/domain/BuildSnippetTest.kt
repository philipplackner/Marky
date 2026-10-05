package com.plcoding.marky.feature.search.domain

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isLessThanOrEqualTo
import assertk.assertions.startsWith
import kotlin.test.Test

class BuildSnippetTest {

    private fun snippet(text: String, query: String, startAtFirstMatch: Boolean = true) =
        buildSnippet(text, findMatches(text, searchWords(query)), startAtFirstMatch)

    private fun Snippet.marked() = matches.map { text.substring(it) }

    @Test
    fun `a match near the start keeps the whole text without ellipsis`() {
        val text = "The blue tiles from Lisbon for Ana, a cookbook for Dad."
        val snippet = snippet(text, "lisbon")
        assertThat(snippet.text).isEqualTo(text)
        assertThat(snippet.marked()).containsExactly("Lisbon")
    }

    @Test
    fun `a later match is cut at a word boundary with a leading ellipsis`() {
        val text = "Agenda for the call. We go through empty states. The offsite in Lisbon is still on."
        val snippet = snippet(text, "lisbon")
        assertThat(snippet.text).isEqualTo("…empty states. The offsite in Lisbon is still on.")
        assertThat(snippet.marked()).containsExactly("Lisbon")
    }

    @Test
    fun `without a space before the match the cut stays in the middle of the word`() {
        val text = "x".repeat(50) + " lisbon"
        val snippet = snippet(text, "lisbon")
        assertThat(snippet.text).startsWith("…")
        assertThat(snippet.marked()).containsExactly("lisbon")
    }

    @Test
    fun `a cut never splits an emoji`() {
        val text = "👍".repeat(20) + " lisbon"
        assertThat(text[text.indexOf("lisbon") - 32].isLowSurrogate()).isEqualTo(true)
        val snippet = snippet(text, "lisbon")
        assertThat(snippet.text[1].isLowSurrogate()).isEqualTo(false)
        assertThat(snippet.marked()).containsExactly("lisbon")
    }

    @Test
    fun `a match deep in a long note is found and the snippet stays short`() {
        val text = "word ".repeat(5_000) + "Lisbon " + "tail ".repeat(5_000)
        val snippet = snippet(text, "lisbon")
        assertThat(snippet.marked()).containsExactly("Lisbon")
        assertThat(snippet.text.length).isLessThanOrEqualTo(301)
    }

    @Test
    fun `a title match shows the start of the text and marks matches inside it`() {
        val text = "Tent and stove. Pack the mats."
        val snippet = snippet(text, "pack", startAtFirstMatch = false)
        assertThat(snippet.text).isEqualTo(text)
        assertThat(snippet.marked()).containsExactly("Pack")
    }

    @Test
    fun `matches past the end of the snippet are dropped`() {
        val text = "Pack " + "word ".repeat(200) + "pack"
        val snippet = snippet(text, "pack", startAtFirstMatch = false)
        assertThat(snippet.marked()).containsExactly("Pack")
    }

    @Test
    fun `empty text gives an empty snippet`() {
        val snippet = buildSnippet("", emptyList(), startAtFirstMatch = true)
        assertThat(snippet.text).isEqualTo("")
        assertThat(snippet.matches).isEmpty()
    }
}
