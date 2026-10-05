package com.plcoding.marky.core.files

import assertk.assertThat
import assertk.assertions.endsWith
import assertk.assertions.isEqualTo
import assertk.assertions.isLessThanOrEqualTo
import kotlin.test.Test

class SanitizeFileNameTest {

    @Test
    fun `ordinary name stays unchanged`() {
        assertThat(sanitizeFileName("Trip to Lisbon.md")).isEqualTo("Trip to Lisbon.md")
    }

    @Test
    fun `reserved characters are replaced`() {
        assertThat(sanitizeFileName("Q3/Q4: plan? \"draft\" <a|b> c\\d*.md"))
            .isEqualTo("Q3_Q4_ plan_ _draft_ _a_b_ c_d_.md")
    }

    @Test
    fun `control characters are replaced`() {
        assertThat(sanitizeFileName("a\nb\tc\u0000d\u007F.md")).isEqualTo("a_b_c_d_.md")
    }

    @Test
    fun `surrounding whitespace is trimmed`() {
        assertThat(sanitizeFileName("  Ideas.md \n")).isEqualTo("Ideas.md")
    }

    @Test
    fun `unicode and emoji are kept`() {
        assertThat(sanitizeFileName("Notizen über 📝 日本.md")).isEqualTo("Notizen über 📝 日本.md")
    }

    @Test
    fun `name of exactly 255 bytes stays unchanged`() {
        val name = "a".repeat(252) + ".md"

        assertThat(sanitizeFileName(name)).isEqualTo(name)
    }

    @Test
    fun `long name is shortened before the extension`() {
        assertThat(sanitizeFileName("a".repeat(300) + ".md")).isEqualTo("a".repeat(252) + ".md")
    }

    @Test
    fun `long name without extension is cut to 255 bytes`() {
        assertThat(sanitizeFileName("a".repeat(300))).isEqualTo("a".repeat(255))
    }

    @Test
    fun `shortening counts UTF-8 bytes and doesn't split multi-byte characters`() {
        assertThat(sanitizeFileName("ü".repeat(200) + ".md")).isEqualTo("ü".repeat(126) + ".md")
    }

    @Test
    fun `shortening doesn't split surrogate pairs`() {
        val result = sanitizeFileName("📝".repeat(100) + ".md")

        assertThat(result).isEqualTo("📝".repeat(63) + ".md")
        assertThat(result.encodeToByteArray().size).isLessThanOrEqualTo(255)
    }

    @Test
    fun `space left at the cut is trimmed`() {
        val result = sanitizeFileName("a".repeat(251) + " bbbbbb.md")

        assertThat(result).isEqualTo("a".repeat(251) + ".md")
        assertThat(result).endsWith(".md")
    }
}
