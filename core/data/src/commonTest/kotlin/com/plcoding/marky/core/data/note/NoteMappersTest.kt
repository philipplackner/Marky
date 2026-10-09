package com.plcoding.marky.core.data.note

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.plcoding.marky.core.domain.note.Note
import kotlin.test.Test
import kotlin.time.Instant

class NoteMappersTest {

    @Test
    fun `note survives a round trip through the entity`() {
        val note = Note(
            id = 42,
            title = "Trip to Lisbon 🇵🇹",
            content = "# Plan\n- [ ] Book flights",
            createdAt = Instant.fromEpochMilliseconds(-1_000),
            lastEditedAt = Instant.fromEpochMilliseconds(1_759_600_000_123),
            isPinned = true
        )

        assertThat(note.toNoteEntity().toNote()).isEqualTo(note)
    }

    @Test
    fun `search text is the content without markdown syntax`() {
        val note = Note(
            id = 1,
            title = "",
            content = "# Plan\nwo*rd* with **bold** and `code`\n- [ ] item",
            createdAt = Instant.fromEpochMilliseconds(0),
            lastEditedAt = Instant.fromEpochMilliseconds(0),
            isPinned = false
        )

        assertThat(note.toNoteEntity().searchText).isEqualTo("Plan word with bold and code item")
    }

    @Test
    fun `sub-millisecond precision is dropped when storing`() {
        val note = Note(
            id = 1,
            title = "",
            content = "",
            createdAt = Instant.fromEpochSeconds(1_759_600_000, nanosecondAdjustment = 123_456_789),
            lastEditedAt = Instant.fromEpochSeconds(1_759_600_000, nanosecondAdjustment = 999_999),
            isPinned = false
        )

        val restored = note.toNoteEntity().toNote()

        assertThat(restored.createdAt).isEqualTo(Instant.fromEpochMilliseconds(1_759_600_000_123))
        assertThat(restored.lastEditedAt).isEqualTo(Instant.fromEpochMilliseconds(1_759_600_000_000))
    }
}
