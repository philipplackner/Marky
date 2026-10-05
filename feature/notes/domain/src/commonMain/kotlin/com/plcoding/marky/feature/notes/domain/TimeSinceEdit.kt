package com.plcoding.marky.feature.notes.domain

import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

/**
 * How long ago a note was edited, following the writing rules:
 * "Just now", "4 min", "2 h", "Yesterday", "3 d", "1 w" (up to 4 weeks), then the date,
 * with the year only for earlier years.
 */
sealed interface TimeSinceEdit {
    data object JustNow : TimeSinceEdit
    data class Minutes(val minutes: Int) : TimeSinceEdit
    data class Hours(val hours: Int) : TimeSinceEdit
    data object Yesterday : TimeSinceEdit
    data class Days(val days: Int) : TimeSinceEdit
    data class Weeks(val weeks: Int) : TimeSinceEdit

    /**
     * @property month 1 to 12.
     * @property year null when the date is in the current year.
     */
    data class Date(val day: Int, val month: Int, val year: Int?) : TimeSinceEdit
}

fun timeSinceEdit(editedAt: Instant, now: Instant, timeZone: TimeZone): TimeSinceEdit {
    val elapsed = now - editedAt
    if (elapsed < 1.minutes) return TimeSinceEdit.JustNow
    if (elapsed < 1.hours) return TimeSinceEdit.Minutes(elapsed.inWholeMinutes.toInt())

    val editedDate = editedAt.toLocalDateTime(timeZone).date
    val today = now.toLocalDateTime(timeZone).date
    val days = editedDate.daysUntil(today)
    return when {
        days <= 0 -> TimeSinceEdit.Hours(elapsed.inWholeHours.toInt())
        days == 1 -> TimeSinceEdit.Yesterday
        days < DaysPerWeek -> TimeSinceEdit.Days(days)
        days < DaysPerWeek * (MaxWeeks + 1) -> TimeSinceEdit.Weeks(days / DaysPerWeek)
        else -> TimeSinceEdit.Date(
            day = editedDate.day,
            month = editedDate.month.ordinal + 1,
            year = editedDate.year.takeIf { it != today.year },
        )
    }
}

private const val DaysPerWeek = 7
private const val MaxWeeks = 4
