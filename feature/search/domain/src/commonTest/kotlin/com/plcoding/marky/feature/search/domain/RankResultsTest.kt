package com.plcoding.marky.feature.search.domain

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import kotlin.test.Test
import kotlin.time.Instant

class RankResultsTest {

    private fun candidate(id: Long, title: String, text: String, editedAt: Long = id) = SearchCandidate(
        noteId = id,
        title = title,
        plainText = text,
        lastEditedAt = Instant.fromEpochMilliseconds(editedAt),
    )

    @Test
    fun `title matches come first even when a text match is newer`() {
        val results = rankResults(
            candidates = listOf(
                candidate(1, "Trip to Lisbon", "Flights are booked.", editedAt = 10),
                candidate(2, "Q4 roadmap call", "The offsite in Lisbon is on.", editedAt = 99),
            ),
            query = "lisbon",
        )
        assertThat(results.inTitles.map { it.noteId }).containsExactly(1L)
        assertThat(results.inText.map { it.noteId }).containsExactly(2L)
    }

    @Test
    fun `a note matching in title and text appears only in the title group`() {
        val results = rankResults(listOf(candidate(1, "Lisbon", "More about Lisbon")), "lisbon")
        assertThat(results.inTitles.map { it.noteId }).containsExactly(1L)
        assertThat(results.inText).isEqualTo(emptyList())
    }

    @Test
    fun `each group is sorted by last edit with ties broken by id`() {
        val results = rankResults(
            candidates = listOf(
                candidate(1, "", "lisbon", editedAt = 5),
                candidate(2, "", "lisbon", editedAt = 9),
                candidate(3, "", "lisbon", editedAt = 5),
            ),
            query = "lisbon",
        )
        assertThat(results.inText.map { it.noteId }).containsExactly(2L, 3L, 1L)
    }

    @Test
    fun `every word has to appear in the title or text`() {
        val results = rankResults(
            candidates = listOf(
                candidate(1, "Trip to Lisbon", "Pack the rain jacket."),
                candidate(2, "Trip to Porto", "Pack the rain jacket."),
            ),
            query = "lisbon pack",
        )
        assertThat(results.inTitles.map { it.noteId }).containsExactly(1L)
        assertThat(results.inText).isEqualTo(emptyList())
    }

    @Test
    fun `candidates that only matched in markdown syntax are dropped`() {
        val results = rankResults(listOf(candidate(1, "Links", "Tram 28")), "carris")
        assertThat(results.isEmpty).isEqualTo(true)
    }

    @Test
    fun `a text match snippet starts near the match and a title match snippet at the start`() {
        val text = "Agenda for the call. We go through empty states. The offsite in Lisbon is still on."
        val textResult = rankResults(listOf(candidate(1, "Call", text)), "lisbon").inText.single()
        val titleResult = rankResults(listOf(candidate(1, "Lisbon call", text)), "lisbon").inTitles.single()
        assertThat(textResult.snippet.text).isEqualTo("…empty states. The offsite in Lisbon is still on.")
        assertThat(titleResult.snippet.text).isEqualTo(text)
        assertThat(titleResult.titleMatches).containsExactly(0..5)
    }

    @Test
    fun `a blank query gives no results`() {
        assertThat(rankResults(listOf(candidate(1, "Lisbon", "")), "  ")).isEqualTo(SearchResults.Empty)
    }
}
