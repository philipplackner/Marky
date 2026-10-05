package com.plcoding.marky.core.data.util

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.plcoding.marky.core.domain.util.DataError
import kotlin.test.Test

class SafeDbCallTest {

    @Test
    fun `message thrown by both drivers on SQLITE_FULL maps to DISK_FULL`() {
        // Captured from Room on the Android emulator and the iOS simulator.
        assertThat(sqliteErrorToDataError("Error code: 13, message: database or disk is full"))
            .isEqualTo(DataError.Local.DISK_FULL)
    }

    @Test
    fun `SQLITE_FULL without a SQLite message maps to DISK_FULL`() {
        assertThat(sqliteErrorToDataError("Error code: 13")).isEqualTo(DataError.Local.DISK_FULL)
    }

    @Test
    fun `extended result code is reduced to its primary code`() {
        // Hypothetical extended code with primary SQLITE_FULL: (1 shl 8) or 13.
        assertThat(sqliteErrorToDataError("Error code: 269, message: x")).isEqualTo(DataError.Local.DISK_FULL)
    }

    @Test
    fun `other result codes map to UNKNOWN even when the message mentions a full disk`() {
        listOf(
            "Error code: 1, message: no such table: full_disk_notes",
            "Error code: 11, message: database disk image is malformed",
            "Error code: 778, message: disk I/O error",
            "Error code: 130, message: database or disk is full",
            "Error code: 131, message: x"
        ).forEach { message ->
            assertThat(sqliteErrorToDataError(message), name = "message=\"$message\"")
                .isEqualTo(DataError.Local.UNKNOWN)
        }
    }

    @Test
    fun `messages without a leading result code map to UNKNOWN`() {
        listOf(
            null,
            "",
            "database or disk is full",
            "Error code: , message: x",
            "Error code: 99999999999, message: x",
            "prefix Error code: 13, message: database or disk is full"
        ).forEach { message ->
            assertThat(sqliteErrorToDataError(message), name = "message=\"$message\"")
                .isEqualTo(DataError.Local.UNKNOWN)
        }
    }
}
