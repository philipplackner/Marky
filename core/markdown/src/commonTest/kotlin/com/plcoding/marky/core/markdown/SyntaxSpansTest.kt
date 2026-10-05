package com.plcoding.marky.core.markdown

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import com.plcoding.marky.core.markdown.SyntaxStyle.Bold
import com.plcoding.marky.core.markdown.SyntaxStyle.Code
import com.plcoding.marky.core.markdown.SyntaxStyle.Heading2
import com.plcoding.marky.core.markdown.SyntaxStyle.Italic
import com.plcoding.marky.core.markdown.SyntaxStyle.Link
import com.plcoding.marky.core.markdown.SyntaxStyle.Syntax
import com.plcoding.marky.core.markdown.SyntaxStyle.Url
import kotlin.test.Test

class SyntaxSpansTest {

    private fun spans(text: String) = markdownSyntaxSpans(text).map { it.style to text.substring(it.start, it.end) }

    @Test
    fun `plain text has no spans`() {
        assertThat(markdownSyntaxSpans("just words")).isEmpty()
        assertThat(markdownSyntaxSpans("")).isEmpty()
    }

    @Test
    fun `heading marks the whole line then the hashes as syntax`() {
        assertThat(spans("## Packing")).containsExactly(Heading2 to "## Packing", Syntax to "##")
    }

    @Test
    fun `bold and italic markers are syntax inside their styled range`() {
        assertThat(spans("**b** *i*")).containsExactly(
            Bold to "**b**", Syntax to "*", Syntax to "*", Syntax to "*", Syntax to "*",
            Italic to "*i*", Syntax to "*", Syntax to "*",
        )
    }

    @Test
    fun `inline code and fences use the code style`() {
        assertThat(spans("`x`")).containsExactly(Code to "`x`", Syntax to "`", Syntax to "`")
        assertThat(spans("```kt\na\n```")).containsExactly(
            Code to "```kt\na\n```", Syntax to "```", Syntax to "kt", Syntax to "```",
        )
    }

    @Test
    fun `link brackets are syntax and the destination is a url`() {
        assertThat(spans("[t](https://x.y)")).containsExactly(
            Link to "[t](https://x.y)", Syntax to "[", Syntax to "]", Syntax to "(", Url to "https://x.y", Syntax to ")",
        )
    }

    @Test
    fun `list checklist and quote markers are syntax`() {
        assertThat(spans("- [ ] a")).containsExactly(Syntax to "- ", Syntax to "[ ] ")
        assertThat(spans("1. a")).containsExactly(Syntax to "1. ")
        assertThat(spans("> a")).containsExactly(Syntax to "> ")
    }
}
