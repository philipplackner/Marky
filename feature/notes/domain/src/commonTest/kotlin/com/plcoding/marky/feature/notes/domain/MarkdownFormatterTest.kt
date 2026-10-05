package com.plcoding.marky.feature.notes.domain

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import com.plcoding.marky.feature.notes.domain.MarkdownFormat.BulletList
import com.plcoding.marky.feature.notes.domain.MarkdownFormat.Checklist
import com.plcoding.marky.feature.notes.domain.MarkdownFormat.Code
import com.plcoding.marky.feature.notes.domain.MarkdownFormat.Heading
import com.plcoding.marky.feature.notes.domain.MarkdownFormat.Italic
import com.plcoding.marky.feature.notes.domain.MarkdownFormat.Link
import com.plcoding.marky.feature.notes.domain.MarkdownFormat.Quote
import kotlin.test.Test

/**
 * Text uses `|` for the cursor and `⟦` `⟧` for a selection, e.g. "a ⟦bc⟧ d".
 */
class MarkdownFormatterTest {

    private fun editor(marked: String): EditorText {
        val cursor = marked.indexOf('|')
        if (cursor >= 0) return EditorText(marked.removeRange(cursor, cursor + 1), cursor)
        val start = marked.indexOf('⟦')
        val end = marked.indexOf('⟧') - 1
        return EditorText(marked.replace("⟦", "").replace("⟧", ""), start, end)
    }

    private fun EditorText.marked(): String = if (hasSelection) {
        text.substring(0, selectionStart) + "⟦" + text.substring(selectionStart, selectionEnd) + "⟧" + text.substring(selectionEnd)
    } else {
        text.substring(0, selectionStart) + "|" + text.substring(selectionStart)
    }

    private fun apply(format: MarkdownFormat, marked: String): String =
        MarkdownFormatter.apply(format, editor(marked), linkTextPlaceholder = "text").marked()

    @Test
    fun `heading cycles one two three then off`() {
        assertThat(apply(Heading, "Pack|ing")).isEqualTo("# Pack|ing")
        assertThat(apply(Heading, "# Pack|ing")).isEqualTo("## Pack|ing")
        assertThat(apply(Heading, "## Pack|ing")).isEqualTo("### Pack|ing")
        assertThat(apply(Heading, "### Pack|ing")).isEqualTo("Pack|ing")
    }

    @Test
    fun `heading only changes the cursor line`() {
        assertThat(apply(Heading, "one\ntw|o\nthree")).isEqualTo("one\n# tw|o\nthree")
    }

    @Test
    fun `heading on an empty note inserts the marker`() {
        assertThat(apply(Heading, "|")).isEqualTo("# |")
    }

    @Test
    fun `cursor inside a removed prefix moves to the line content`() {
        assertThat(apply(Heading, "##|# x")).isEqualTo("|x")
    }

    @Test
    fun `bold wraps the selection and keeps it selected`() {
        assertThat(apply(MarkdownFormat.Bold, "the ⟦tram pass⟧.")).isEqualTo("the **⟦tram pass⟧**.")
    }

    @Test
    fun `bold leaves surrounding whitespace of the selection outside the markers`() {
        assertThat(apply(MarkdownFormat.Bold, "a⟦ word ⟧b")).isEqualTo("a **⟦word⟧** b")
    }

    @Test
    fun `bold on bold text removes the markers`() {
        assertThat(apply(MarkdownFormat.Bold, "**⟦word⟧**")).isEqualTo("⟦word⟧")
        assertThat(apply(MarkdownFormat.Bold, "⟦**word**⟧")).isEqualTo("⟦word⟧")
    }

    @Test
    fun `bold without selection inserts a pair with the cursor inside and a second tap removes it`() {
        assertThat(apply(MarkdownFormat.Bold, "a |b")).isEqualTo("a **|**b")
        assertThat(apply(MarkdownFormat.Bold, "a **|**b")).isEqualTo("a |b")
    }

    @Test
    fun `italic inside bold wraps instead of unwrapping`() {
        assertThat(apply(Italic, "**⟦word⟧**")).isEqualTo("***⟦word⟧***")
        assertThat(apply(Italic, "***⟦word⟧***")).isEqualTo("**⟦word⟧**")
        assertThat(apply(Italic, "*⟦word⟧*")).isEqualTo("⟦word⟧")
    }

    @Test
    fun `wrapping around emoji keeps surrogate pairs intact`() {
        assertThat(apply(MarkdownFormat.Bold, "go ⟦👨‍👩‍👧 🇵🇹⟧")).isEqualTo("go **⟦👨‍👩‍👧 🇵🇹⟧**")
    }

    @Test
    fun `whitespace only selection inserts a pair at its end`() {
        assertThat(apply(MarkdownFormat.Bold, "a⟦  ⟧b")).isEqualTo("a  **|**b")
    }

    @Test
    fun `list checklist and quote add their marker and a second tap removes it`() {
        assertThat(apply(BulletList, "Pass|port")).isEqualTo("- Pass|port")
        assertThat(apply(BulletList, "- Pass|port")).isEqualTo("Pass|port")
        assertThat(apply(Checklist, "Pass|port")).isEqualTo("- [ ] Pass|port")
        assertThat(apply(Checklist, "- [x] Pass|port")).isEqualTo("Pass|port")
        assertThat(apply(Quote, "Go| early")).isEqualTo("> Go| early")
        assertThat(apply(Quote, "> Go| early")).isEqualTo("Go| early")
    }

    @Test
    fun `list markers replace each other and keep indentation`() {
        assertThat(apply(Checklist, "  - a|")).isEqualTo("  - [ ] a|")
        assertThat(apply(BulletList, "3. a|")).isEqualTo("- a|")
        assertThat(apply(BulletList, "- [ ] a|")).isEqualTo("- a|")
    }

    @Test
    fun `list on several lines marks every non blank line`() {
        assertThat(apply(Checklist, "⟦a\n\nb⟧")).isEqualTo("- [ ] ⟦a\n\n- [ ] b⟧")
        assertThat(apply(Checklist, "- [ ] ⟦a\n\n- [x] b⟧")).isEqualTo("⟦a\n\nb⟧")
    }

    @Test
    fun `list on mixed lines adds the marker where it is missing`() {
        assertThat(apply(BulletList, "⟦- a\nb⟧")).isEqualTo("- ⟦a\n- b⟧")
    }

    @Test
    fun `line marker on an empty line inserts it`() {
        assertThat(apply(Checklist, "a\n|")).isEqualTo("a\n- [ ] |")
    }

    @Test
    fun `code wraps a one line selection in backticks`() {
        assertThat(apply(Code, "ref ⟦X7QK2⟧ ok")).isEqualTo("ref `⟦X7QK2⟧` ok")
        assertThat(apply(Code, "ref `⟦X7QK2⟧` ok")).isEqualTo("ref ⟦X7QK2⟧ ok")
    }

    @Test
    fun `code without selection fences the current line and a second tap removes the fence`() {
        assertThat(apply(Code, "a\nco|de\nb")).isEqualTo("a\n```\nco|de\n```\nb")
        assertThat(apply(Code, "a\n```\nco|de\n```\nb")).isEqualTo("a\nco|de\nb")
    }

    @Test
    fun `code on an empty note gives an empty fence with the cursor inside`() {
        assertThat(apply(Code, "|")).isEqualTo("```\n|\n```")
    }

    @Test
    fun `code over several lines fences all of them`() {
        assertThat(apply(Code, "x⟦a\nb⟧")).isEqualTo("```\nx⟦a\nb⟧\n```")
    }

    @Test
    fun `link without selection inserts a placeholder and selects the url`() {
        assertThat(apply(Link, "see |")).isEqualTo("see [text](⟦https://⟧)")
    }

    @Test
    fun `link uses the selection as its text`() {
        assertThat(apply(Link, "⟦Tram 28⟧ route")).isEqualTo("[Tram 28](⟦https://⟧) route")
    }

    @Test
    fun `active line formats follow the cursor line`() {
        assertThat(MarkdownFormatter.activeLineFormats(editor("- [ ] a|"))).isEqualTo(setOf(Checklist))
        assertThat(MarkdownFormatter.activeLineFormats(editor("## a|"))).isEqualTo(setOf(Heading))
        assertThat(MarkdownFormatter.activeLineFormats(editor("- a\nb|"))).isEmpty()
        assertThat(MarkdownFormatter.activeLineFormats(editor("|"))).isEmpty()
    }
}
