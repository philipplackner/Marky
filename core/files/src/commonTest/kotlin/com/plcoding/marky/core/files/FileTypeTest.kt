package com.plcoding.marky.core.files

import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import kotlin.test.Test

class FileTypeTest {

    private val markdown = FileType(extensions = listOf("md"), mimeTypes = listOf("text/markdown"))

    @Test
    fun `file with a listed extension matches`() {
        assertThat(markdown.matches("ideas.md")).isTrue()
    }

    @Test
    fun `extension check ignores case on both sides`() {
        assertThat(markdown.matches("IDEAS.MD")).isTrue()
        assertThat(FileType(listOf("MD"), emptyList()).matches("ideas.md")).isTrue()
    }

    @Test
    fun `only the last extension counts`() {
        assertThat(markdown.matches("recipes.md.pdf")).isFalse()
        assertThat(markdown.matches("recipes.pdf.md")).isTrue()
    }

    @Test
    fun `names without an extension don't match`() {
        listOf("README", "notes.", "", "md").forEach { name ->
            assertThat(markdown.matches(name), name = "name=\"$name\"").isFalse()
        }
    }

    @Test
    fun `extension that only starts with a listed one doesn't match`() {
        assertThat(markdown.matches("notes.markdown")).isFalse()
        assertThat(markdown.matches("notes.mdx")).isFalse()
    }

    @Test
    fun `dot file with a listed extension matches`() {
        assertThat(markdown.matches(".md")).isTrue()
    }

    @Test
    fun `unicode and emoji in the name don't affect the check`() {
        assertThat(markdown.matches("Notizen über 📝.md")).isTrue()
    }

    @Test
    fun `any of several extensions matches`() {
        val text = FileType(listOf("md", "txt"), emptyList())

        assertThat(text.matches("a.txt")).isTrue()
        assertThat(text.matches("a.md")).isTrue()
        assertThat(text.matches("a.pdf")).isFalse()
    }

    @Test
    fun `no extensions accepts every file`() {
        val any = FileType(emptyList(), emptyList())

        listOf("recipes.pdf", "README", "").forEach { name ->
            assertThat(any.matches(name), name = "name=\"$name\"").isTrue()
        }
    }
}
