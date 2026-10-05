package com.plcoding.marky.core.markdown

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import com.plcoding.marky.core.markdown.MarkdownBlock.BulletItem
import com.plcoding.marky.core.markdown.MarkdownBlock.ChecklistItem
import com.plcoding.marky.core.markdown.MarkdownBlock.CodeBlock
import com.plcoding.marky.core.markdown.MarkdownBlock.Heading
import com.plcoding.marky.core.markdown.MarkdownBlock.NumberedItem
import com.plcoding.marky.core.markdown.MarkdownBlock.Paragraph
import com.plcoding.marky.core.markdown.MarkdownBlock.Quote
import com.plcoding.marky.core.markdown.MarkdownInline.Bold
import com.plcoding.marky.core.markdown.MarkdownInline.Code
import com.plcoding.marky.core.markdown.MarkdownInline.Italic
import com.plcoding.marky.core.markdown.MarkdownInline.Link
import com.plcoding.marky.core.markdown.MarkdownInline.Text
import kotlin.test.Test

class ParseMarkdownTest {

    private fun blocks(text: String) = parseMarkdown(text).blocks

    @Test
    fun `empty and blank text give no blocks`() {
        assertThat(blocks("")).isEmpty()
        assertThat(blocks(" \n\n \t")).isEmpty()
    }

    @Test
    fun `headings one to three keep their level and inline formatting`() {
        assertThat(blocks("# One **b**\n## Two\n### Three")).containsExactly(
            Heading(1, listOf(Text("One "), Bold(listOf(Text("b"))))),
            Heading(2, listOf(Text("Two"))),
            Heading(3, listOf(Text("Three"))),
        )
    }

    @Test
    fun `headings four to six stay plain text with their syntax`() {
        assertThat(blocks("#### Four\n###### Six")).containsExactly(
            Paragraph(listOf(Text("#### Four"))),
            Paragraph(listOf(Text("###### Six"))),
        )
    }

    @Test
    fun `hash without a space is a paragraph and not a heading`() {
        assertThat(blocks("#tag and #another")).containsExactly(Paragraph(listOf(Text("#tag and #another"))))
    }

    @Test
    fun `inline formatting nests`() {
        assertThat(blocks("a **bold *both*** _it_ `x` end")).containsExactly(
            Paragraph(
                listOf(
                    Text("a "),
                    Bold(listOf(Text("bold "), Italic(listOf(Text("both"))))),
                    Text(" "),
                    Italic(listOf(Text("it"))),
                    Text(" "),
                    Code("x"),
                    Text(" end"),
                ),
            ),
        )
    }

    @Test
    fun `inline code keeps markdown characters literally`() {
        assertThat(blocks("`**not bold**`")).containsExactly(Paragraph(listOf(Code("**not bold**"))))
    }

    @Test
    fun `inline code strips one surrounding space`() {
        assertThat(blocks("`` ` ``")).containsExactly(Paragraph(listOf(Code("`"))))
    }

    @Test
    fun `links keep text formatting and url`() {
        assertThat(blocks("[Tram **28**](https://carris.pt)")).containsExactly(
            Paragraph(listOf(Link(listOf(Text("Tram "), Bold(listOf(Text("28")))), "https://carris.pt"))),
        )
    }

    @Test
    fun `bare and angle bracket urls become links`() {
        assertThat(blocks("see https://a.com and <https://b.com> or www.c.com")).containsExactly(
            Paragraph(
                listOf(
                    Text("see "),
                    Link(listOf(Text("https://a.com")), "https://a.com"),
                    Text(" and "),
                    Link(listOf(Text("https://b.com")), "https://b.com"),
                    Text(" or "),
                    Link(listOf(Text("www.c.com")), "https://www.c.com"),
                ),
            ),
        )
    }

    @Test
    fun `line breaks inside a paragraph are kept`() {
        assertThat(blocks("line one\n   line two")).containsExactly(Paragraph(listOf(Text("line one\nline two"))))
    }

    @Test
    fun `unsupported inline syntax stays as written`() {
        assertThat(blocks("~~gone~~ ![img](u.png)")).containsExactly(Paragraph(listOf(Text("~~gone~~ ![img](u.png)"))))
    }

    @Test
    fun `bullet markers all become bullet items`() {
        assertThat(blocks("- a\n- b\n\n* c\n\n+ d")).containsExactly(
            BulletItem(listOf(Text("a"))),
            BulletItem(listOf(Text("b"))),
            BulletItem(listOf(Text("c"))),
            BulletItem(listOf(Text("d"))),
        )
    }

    @Test
    fun `numbered list counts up from its first number`() {
        assertThat(blocks("3. three\n1. next\n1. last")).containsExactly(
            NumberedItem(3, listOf(Text("three"))),
            NumberedItem(4, listOf(Text("next"))),
            NumberedItem(5, listOf(Text("last"))),
        )
    }

    @Test
    fun `checklist items know their state and checkbox offset`() {
        val text = "Intro\n\n- [x] Passport\n- [ ] Rain **jacket**\n- [X] Upper"
        assertThat(blocks(text)).containsExactly(
            Paragraph(listOf(Text("Intro"))),
            ChecklistItem(checked = true, checkboxOffset = text.indexOf("[x]"), content = listOf(Text("Passport"))),
            ChecklistItem(
                checked = false,
                checkboxOffset = text.indexOf("[ ]"),
                content = listOf(Text("Rain "), Bold(listOf(Text("jacket")))),
            ),
            ChecklistItem(checked = true, checkboxOffset = text.indexOf("[X]"), content = listOf(Text("Upper"))),
        )
    }

    @Test
    fun `checkbox offsets are char offsets after emoji`() {
        val text = "👨‍👩‍👧 🇵🇹\n\n- [ ] 🎒 bag"
        val item = blocks(text).last() as ChecklistItem
        assertThat(item.checkboxOffset).isEqualTo(text.indexOf("[ ]"))
        assertThat(item.content).containsExactly(Text("🎒 bag"))
    }

    @Test
    fun `nested bullet items become children of their parent`() {
        assertThat(blocks("- parent\n  - child\n- next")).containsExactly(
            BulletItem(listOf(Text("parent")), children = listOf(BulletItem(listOf(Text("child"))))),
            BulletItem(listOf(Text("next"))),
        )
    }

    @Test
    fun `lists nest three levels deep`() {
        assertThat(blocks("- Food\n  - Pastéis de nata\n    - Manteigaria\n- Sights")).containsExactly(
            BulletItem(
                listOf(Text("Food")),
                children = listOf(
                    BulletItem(
                        listOf(Text("Pastéis de nata")),
                        children = listOf(BulletItem(listOf(Text("Manteigaria")))),
                    ),
                ),
            ),
            BulletItem(listOf(Text("Sights"))),
        )
    }

    @Test
    fun `nested numbered items keep their own numbering`() {
        assertThat(blocks("1. Check in\n   1. Print boarding pass\n   2. Drop bags\n2. Security")).containsExactly(
            NumberedItem(
                number = 1,
                content = listOf(Text("Check in")),
                children = listOf(
                    NumberedItem(number = 1, content = listOf(Text("Print boarding pass"))),
                    NumberedItem(number = 2, content = listOf(Text("Drop bags"))),
                ),
            ),
            NumberedItem(number = 2, content = listOf(Text("Security"))),
        )
    }

    @Test
    fun `different list kinds nest inside each other`() {
        val text = "1. Pack\n   - [x] Passport\n- [ ] Book tram\n  - Line 28"
        assertThat(blocks(text)).containsExactly(
            NumberedItem(
                number = 1,
                content = listOf(Text("Pack")),
                children = listOf(
                    ChecklistItem(checked = true, checkboxOffset = text.indexOf("[x]"), content = listOf(Text("Passport"))),
                ),
            ),
            ChecklistItem(
                checked = false,
                checkboxOffset = text.indexOf("[ ]"),
                content = listOf(Text("Book tram")),
                children = listOf(BulletItem(listOf(Text("Line 28")))),
            ),
        )
    }

    @Test
    fun `quote joins its lines without markers`() {
        assertThat(blocks("> Go **early**.\n> It's packed")).containsExactly(
            Quote(listOf(Text("Go "), Bold(listOf(Text("early"))), Text(".\nIt's packed"))),
        )
    }

    @Test
    fun `fenced code keeps spacing and drops fences and language`() {
        assertThat(blocks("```kotlin\ncode blocks keep\n  their spacing\n```")).containsExactly(
            CodeBlock("code blocks keep\n  their spacing"),
        )
    }

    @Test
    fun `unclosed fence runs to the end of the note`() {
        assertThat(blocks("```\nopen")).containsExactly(CodeBlock("open"))
    }

    @Test
    fun `empty fence gives an empty code block`() {
        assertThat(blocks("```\n```")).containsExactly(CodeBlock(""))
    }

    @Test
    fun `tables and rules stay as written`() {
        assertThat(blocks("---\n\n| a |\n|---|\n| 1 |")).containsExactly(
            Paragraph(listOf(Text("---"))),
            Paragraph(listOf(Text("| a |\n|---|\n| 1 |"))),
        )
    }
}
