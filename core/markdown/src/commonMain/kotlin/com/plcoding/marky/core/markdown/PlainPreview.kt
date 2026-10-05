package com.plcoding.marky.core.markdown

/**
 * The note text without Markdown syntax and with line breaks collapsed, as shown in
 * list rows and pinned tiles.
 *
 * Only the start of long notes is parsed, since previews show at most two lines.
 *
 * @return an empty string if the note has no visible text.
 */
fun String.toPlainPreview(maxLength: Int = 300): String {
    val document = parseMarkdown(take(PreviewSourceLength))
    return document.blocks
        .joinToString(" ") { it.plainText() }
        .replace(Whitespace, " ")
        .trim()
        .take(maxLength)
}

/**
 * The visible text of [inlines] without any syntax.
 */
fun plainText(inlines: List<MarkdownInline>): String = inlines.joinToString("") { inline ->
    when (inline) {
        is MarkdownInline.Text -> inline.text
        is MarkdownInline.Bold -> plainText(inline.children)
        is MarkdownInline.Italic -> plainText(inline.children)
        is MarkdownInline.Code -> inline.code
        is MarkdownInline.Link -> plainText(inline.children)
    }
}

private fun MarkdownBlock.plainText(): String = when (this) {
    is MarkdownBlock.Heading -> plainText(content)
    is MarkdownBlock.Paragraph -> plainText(content)
    is MarkdownBlock.BulletItem -> plainText(content)
    is MarkdownBlock.NumberedItem -> plainText(content)
    is MarkdownBlock.ChecklistItem -> plainText(content)
    is MarkdownBlock.Quote -> plainText(content)
    is MarkdownBlock.CodeBlock -> code
}

private const val PreviewSourceLength = 2_000
private val Whitespace = Regex("\\s+")
