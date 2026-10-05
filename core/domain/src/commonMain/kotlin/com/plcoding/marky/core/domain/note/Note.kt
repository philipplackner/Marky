package com.plcoding.marky.core.domain.note

import kotlin.time.Instant

/**
 * A single Markdown note.
 *
 * @property id 0 for a note that hasn't been stored yet.
 * @property content the raw Markdown text.
 */
data class Note(
    val id: Long,
    val title: String,
    val content: String,
    val createdAt: Instant,
    val lastEditedAt: Instant,
    val isPinned: Boolean
)
