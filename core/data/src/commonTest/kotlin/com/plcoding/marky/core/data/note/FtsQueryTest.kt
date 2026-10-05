package com.plcoding.marky.core.data.note

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import kotlin.test.Test

class FtsQueryTest {

    @Test
    fun `input without letters or digits gives no query`() {
        listOf("", "   ", "\n\t", "\"*-:()^", "👍🏽👨‍👩‍👧‍👦", "​").forEach { input ->
            assertThat(input.toFtsQuery(), name = "input=\"$input\"").isNull()
        }
    }

    @Test
    fun `single word becomes a prefix term`() {
        assertThat("lisbon".toFtsQuery()).isEqualTo("lisbon*")
    }

    @Test
    fun `words separated by repeated whitespace become separate terms`() {
        assertThat("  trip \n\t to   lisbon ".toFtsQuery()).isEqualTo("trip* to* lisbon*")
    }

    @Test
    fun `uppercase operators are searched as plain words`() {
        assertThat("cats OR dogs NOT birds NEAR AND".toFtsQuery())
            .isEqualTo("cats* or* dogs* not* birds* near* and*")
    }

    @Test
    fun `FTS syntax characters split words instead of reaching the query`() {
        assertThat("\"exact\" -minus col:value (group) ^start wild*card".toFtsQuery())
            .isEqualTo("exact* minus* col* value* group* start* wild* card*")
    }

    @Test
    fun `emoji between letters acts as a separator`() {
        assertThat("good👍🏽idea".toFtsQuery()).isEqualTo("good* idea*")
    }

    @Test
    fun `zero width space splits words`() {
        assertThat("note​book".toFtsQuery()).isEqualTo("note* book*")
    }

    @Test
    fun `non-Latin letters and digits are kept`() {
        assertThat("Привет 日本語 مرحبا 2026".toFtsQuery()).isEqualTo("привет* 日本語* مرحبا* 2026*")
    }

    @Test
    fun `precomposed accent is kept inside the word`() {
        assertThat("Café".toFtsQuery()).isEqualTo("café*")
    }

    @Test
    fun `decomposed accent leaves the base word as prefix`() {
        assertThat("café".toFtsQuery()).isEqualTo("cafe*")
    }
}
