package com.plcoding.marky.feature.search.domain

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import kotlin.test.Test

class FindMatchesTest {

    private fun matches(text: String, query: String) = findMatches(text, searchWords(query))

    private fun marked(text: String, query: String) = matches(text, query).map { text.substring(it) }

    @Test
    fun `case is ignored`() {
        assertThat(marked("Trip to LISBON", "lisbon")).containsExactly("LISBON")
    }

    @Test
    fun `accents are ignored in the text and in the query`() {
        assertThat(marked("Café in Lisbôa", "cafe lisboa")).containsExactly("Café", "Lisbôa")
        assertThat(marked("Cafe in Lisboa", "CAFÉ")).containsExactly("Cafe")
    }

    @Test
    fun `combining accents stay inside the marked range`() {
        val text = "Café au lait"
        assertThat(marked(text, "cafe")).containsExactly("Café")
    }

    @Test
    fun `only the start of a word matches`() {
        assertThat(marked("Packing, unpacked, pack", "pack")).containsExactly("Pack", "pack")
        assertThat(matches("Packing", "ack")).isEmpty()
    }

    @Test
    fun `a match is marked up to the typed length`() {
        assertThat(marked("Packing", "pack")).containsExactly("Pack")
    }

    @Test
    fun `every match is marked`() {
        val text = "and a rain jacket. Packing: passport. It's packed by noon."
        assertThat(marked(text, "pack")).containsExactly("Pack", "pack")
    }

    @Test
    fun `words after punctuation and digits count as word starts`() {
        assertThat(marked("(lisbon) 2024-lisbon", "lisbon")).containsExactly("lisbon", "lisbon")
        assertThat(matches("2lisbon", "lisbon")).isEmpty()
    }

    @Test
    fun `overlapping matches of different words are merged`() {
        assertThat(matches("Packing", "pa pack")).containsExactly(0..3)
    }

    @Test
    fun `ranges point into the original text after emoji`() {
        val text = "👨‍👩‍👧 Lisbon 👍🏽 lisbon"
        assertThat(marked(text, "lisbon")).containsExactly("Lisbon", "lisbon")
    }

    @Test
    fun `non latin scripts match`() {
        assertThat(marked("Поездка в Лиссабон", "лисс")).containsExactly("Лисс")
    }

    @Test
    fun `queries without letters or digits match nothing`() {
        listOf("", "   ", "!?*", "\"", "-").forEach { query ->
            assertThat(matches("Some text - with \"quotes\"!", query), name = "query=\"$query\"").isEmpty()
        }
    }

    @Test
    fun `fts operators are searched as words`() {
        assertThat(marked("Salt OR pepper", "or")).containsExactly("OR")
    }

    @Test
    fun `the search words are folded and deduplicated`() {
        assertThat(searchWords("  Lisbôa, LISBOA  café ")).isEqualTo(listOf("lisboa", "cafe"))
    }
}
