package com.plcoding.marky.feature.notes.domain

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.test.Test
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class TimeSinceEditTest {

    private val zone = TimeZone.of("Europe/Lisbon")
    private val now = LocalDateTime(2026, 8, 20, 15, 0).toInstant(zone)

    private fun ago(duration: Duration) = timeSinceEdit(now - duration, now, zone)

    @Test
    fun `under a minute is just now`() {
        assertThat(ago(Duration.ZERO)).isEqualTo(TimeSinceEdit.JustNow)
        assertThat(ago(59.seconds)).isEqualTo(TimeSinceEdit.JustNow)
    }

    @Test
    fun `edits in the future count as just now`() {
        assertThat(ago((-5).minutes)).isEqualTo(TimeSinceEdit.JustNow)
    }

    @Test
    fun `minutes then hours on the same day`() {
        assertThat(ago(1.minutes)).isEqualTo(TimeSinceEdit.Minutes(1))
        assertThat(ago(59.minutes)).isEqualTo(TimeSinceEdit.Minutes(59))
        assertThat(ago(1.hours)).isEqualTo(TimeSinceEdit.Hours(1))
        assertThat(ago(15.hours)).isEqualTo(TimeSinceEdit.Hours(15))
    }

    @Test
    fun `previous calendar day is yesterday even when less than a day ago`() {
        assertThat(ago(16.hours)).isEqualTo(TimeSinceEdit.Yesterday)
        assertThat(ago(1.days + 8.hours)).isEqualTo(TimeSinceEdit.Yesterday)
    }

    @Test
    fun `less than an hour ago across midnight is still minutes`() {
        val justAfterMidnight = LocalDateTime(2026, 8, 20, 0, 10).toInstant(zone)
        assertThat(timeSinceEdit(justAfterMidnight - 20.minutes, justAfterMidnight, zone))
            .isEqualTo(TimeSinceEdit.Minutes(20))
    }

    @Test
    fun `days then weeks up to four`() {
        assertThat(ago(2.days)).isEqualTo(TimeSinceEdit.Days(2))
        assertThat(ago(6.days)).isEqualTo(TimeSinceEdit.Days(6))
        assertThat(ago(7.days)).isEqualTo(TimeSinceEdit.Weeks(1))
        assertThat(ago(34.days)).isEqualTo(TimeSinceEdit.Weeks(4))
    }

    @Test
    fun `older edits show the date with the year only for earlier years`() {
        assertThat(ago(35.days)).isEqualTo(TimeSinceEdit.Date(day = 16, month = 7, year = null))
        val lastYear = LocalDateTime(2025, 12, 31, 23, 30).toInstant(zone)
        assertThat(timeSinceEdit(lastYear, now, zone)).isEqualTo(TimeSinceEdit.Date(day = 31, month = 12, year = 2025))
    }

    @Test
    fun `calendar days follow the given time zone`() {
        val utcNow = LocalDateTime(2026, 8, 20, 0, 30).toInstant(TimeZone.UTC)
        val edited = utcNow - 2.hours
        assertThat(timeSinceEdit(edited, utcNow, TimeZone.UTC)).isEqualTo(TimeSinceEdit.Yesterday)
        assertThat(timeSinceEdit(edited, utcNow, TimeZone.of("Asia/Tokyo"))).isEqualTo(TimeSinceEdit.Hours(2))
    }
}
