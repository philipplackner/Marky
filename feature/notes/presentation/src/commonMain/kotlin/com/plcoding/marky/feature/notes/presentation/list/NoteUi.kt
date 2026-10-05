package com.plcoding.marky.feature.notes.presentation.list

import com.plcoding.marky.feature.notes.domain.TimeSinceEdit

/**
 * @property title null when the note has no title ("Untitled").
 * @property preview Plain text without Markdown; null when the note has no text ("No text yet").
 */
data class NoteUi(
    val id: Long,
    val title: String?,
    val preview: String?,
    val timeSinceEdit: TimeSinceEdit,
)
