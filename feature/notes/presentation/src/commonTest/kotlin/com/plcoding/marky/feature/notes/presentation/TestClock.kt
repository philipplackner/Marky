package com.plcoding.marky.feature.notes.presentation

import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Instant

class TestClock(var now: Instant = Instant.fromEpochMilliseconds(1_786_000_000_000)) : Clock {
    override fun now(): Instant = now

    fun advance(duration: Duration) {
        now += duration
    }
}
