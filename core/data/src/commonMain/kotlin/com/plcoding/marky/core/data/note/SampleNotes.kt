package com.plcoding.marky.core.data.note

import androidx.room.RoomDatabase
import androidx.sqlite.SQLiteConnection
import com.plcoding.marky.core.domain.note.Note
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant

/**
 * Inserts the sample notes when the database file is first created, or recreated after
 * a schema change, so users never start with an empty list.
 */
internal class SampleNotesCallback(
    private val clock: Clock,
) : RoomDatabase.Callback() {

    private var wasRecreated = false

    override fun onCreate(connection: SQLiteConnection) {
        insertSamples(connection)
    }

    // Room drops the tables before this call and creates them again after it, so the
    // samples are inserted once the database is open.
    override fun onDestructiveMigration(connection: SQLiteConnection) {
        wasRecreated = true
    }

    override fun onOpen(connection: SQLiteConnection) {
        if (wasRecreated) {
            wasRecreated = false
            insertSamples(connection)
        }
    }

    private fun insertSamples(connection: SQLiteConnection) {
        sampleNotes(clock.now()).forEach { note ->
            val entity = note.toNoteEntity()
            connection.prepare(
                "INSERT INTO notes (title, content, searchText, createdAt, lastEditedAt, isPinned) VALUES (?, ?, ?, ?, ?, ?)"
            ).use { statement ->
                statement.bindText(1, entity.title)
                statement.bindText(2, entity.content)
                statement.bindText(3, entity.searchText)
                statement.bindLong(4, entity.createdAt)
                statement.bindLong(5, entity.lastEditedAt)
                statement.bindBoolean(6, entity.isPinned)
                statement.step()
            }
        }
    }
}

/**
 * The notes a new user starts with. They are one millisecond apart, so they keep this
 * order in the list sorted by last edit.
 */
internal fun sampleNotes(now: Instant): List<Note> =
    SampleNotes.mapIndexed { index, (title, content, isPinned) ->
        val time = now - index.milliseconds
        Note(
            id = 0,
            title = title,
            content = content,
            createdAt = time,
            lastEditedAt = time,
            isPinned = isPinned,
        )
    }

private val SampleNotes = listOf(
    Triple(
        "Welcome to Marky",
        """
        |Marky keeps notes as **Markdown**: plain text with a few symbols for formatting. Tap *Write* to see them.
        |
        |# Heading 1
        |## Heading 2
        |### Heading 3
        |
        |Text can be **bold**, *italic* or `inline code`.
        |
        |- A bullet list
        |- Pin notes you open often
        |
        |1. A numbered list
        |2. Export any note as .md
        |
        |- [x] Open this note
        |- [ ] Tick this box
        |
        |> Formatting lives in the text, so your notes work in any editor.
        |
        |```
        |code blocks keep
        |  their spacing
        |```
        |
        |[A link opens in the browser](https://www.markdownguide.org)
        """.trimMargin(),
        true,
    ),
    Triple(
        "Markdown basics",
        """
        |Headings, bold, italic, lists and links in one page.
        |
        |# A big heading
        |## A section heading
        |### A small heading
        |
        |Put two stars around words for **bold** and one star for *italic*. Put `backticks` around code.
        |
        |- Start a line with a dash for a bullet list
        |- Like this
        |
        |1. Start a line with a number for a numbered list
        |2. Like this
        |
        |> Start a line with > for a quote.
        |
        |[Links look like this](https://www.markdownguide.org/basic-syntax/)
        """.trimMargin(),
        false,
    ),
    Triple(
        "A sample checklist",
        """
        |Switch to Read and tick the boxes.
        |
        |- [x] Open the sample checklist
        |- [ ] Write a note
        |- [ ] Pin a note you open often
        |- [ ] Search for a word
        """.trimMargin(),
        false,
    ),
    Triple(
        "Import and export",
        """
        |Bring in any .md file, or take a note to another app.
        |
        |## Import
        |Tap the import button on the notes list and choose a .md file. It becomes a new note named after the file.
        |
        |## Export
        |Open a note, tap the menu and choose **Export as .md**. The note is saved as one file named after its title.
        """.trimMargin(),
        false,
    ),
)
