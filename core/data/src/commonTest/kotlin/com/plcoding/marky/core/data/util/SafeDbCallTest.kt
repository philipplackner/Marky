package com.plcoding.marky.core.data.util

import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import kotlin.test.Test

class SafeDbCallTest {

    @Test
    fun `SQLITE_FULL messages are detected as disk full`() {
        listOf(
            "Error code: 13, message: database or disk is full",
            "Error code: 13, message: ",
            "DATABASE OR DISK IS FULL"
        ).forEach { message ->
            assertThat(isDiskFullMessage(message), name = "message=\"$message\"").isTrue()
        }
    }

    @Test
    fun `other SQLite messages are not disk full`() {
        listOf(
            null,
            "",
            "Error code: 11, message: database disk image is malformed",
            "Error code: 130, message: something else",
            "Error code: 1, message: no such table: fullnotes"
        ).forEach { message ->
            assertThat(isDiskFullMessage(message), name = "message=\"$message\"").isFalse()
        }
    }
}
