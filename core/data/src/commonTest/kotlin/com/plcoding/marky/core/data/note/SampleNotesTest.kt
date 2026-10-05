package com.plcoding.marky.core.data.note

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import kotlin.test.Test
import kotlin.time.Instant

class SampleNotesTest {

    private val now = Instant.fromEpochMilliseconds(1_700_000_000_000)

    @Test
    fun `welcome note is the only pinned sample and comes first`() {
        val notes = sampleNotes(now)
        assertThat(notes.map { it.title to it.isPinned }).containsExactly(
            "Welcome to Marky" to true,
            "Markdown basics" to false,
            "A sample checklist" to false,
            "Import and export" to false,
        )
    }

    @Test
    fun `samples are unsaved and ordered by last edit`() {
        val notes = sampleNotes(now)
        notes.forEach { note ->
            assertThat(note.id).isEqualTo(0L)
            assertThat(note.createdAt).isEqualTo(note.lastEditedAt)
        }
        assertThat(notes.map { it.lastEditedAt }).isEqualTo(notes.map { it.lastEditedAt }.sortedDescending())
        assertThat(notes.first().lastEditedAt).isEqualTo(now)
    }
}
