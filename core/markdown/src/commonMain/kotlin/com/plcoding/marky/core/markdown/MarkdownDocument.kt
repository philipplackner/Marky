package com.plcoding.marky.core.markdown

/**
 * A parsed note, reduced to the nodes Marky can show in Read mode.
 * Syntax Marky doesn't support (H4–H6, tables, rules, images, strikethrough, HTML)
 * stays as plain text, exactly as written.
 */
data class MarkdownDocument(
    val blocks: List<MarkdownBlock>,
)

sealed interface MarkdownBlock {

    /** @property level 1, 2 or 3. */
    data class Heading(
        val level: Int,
        val content: List<MarkdownInline>,
    ) : MarkdownBlock

    data class Paragraph(
        val content: List<MarkdownInline>,
    ) : MarkdownBlock

    /**
     * @property children Blocks indented under this item, such as a nested list.
     */
    data class BulletItem(
        val content: List<MarkdownInline>,
        val children: List<MarkdownBlock> = emptyList(),
    ) : MarkdownBlock

    /**
     * @property children Blocks indented under this item, such as a nested list.
     */
    data class NumberedItem(
        val number: Int,
        val content: List<MarkdownInline>,
        val children: List<MarkdownBlock> = emptyList(),
    ) : MarkdownBlock

    /**
     * @property checkboxOffset Offset of the `[` of this item's `[ ]` / `[x]` in the
     * source text. Pass it to [toggleCheckbox] to tick or untick the item.
     * @property children Blocks indented under this item, such as a nested list.
     */
    data class ChecklistItem(
        val checked: Boolean,
        val checkboxOffset: Int,
        val content: List<MarkdownInline>,
        val children: List<MarkdownBlock> = emptyList(),
    ) : MarkdownBlock

    /**
     * @param children The quoted blocks: paragraphs, lists, headings, code or nested quotes.
     */
    data class Quote(
        val children: List<MarkdownBlock>,
    ) : MarkdownBlock

    data class CodeBlock(
        val code: String,
    ) : MarkdownBlock
}

sealed interface MarkdownInline {

    data class Text(val text: String) : MarkdownInline

    data class Bold(val children: List<MarkdownInline>) : MarkdownInline

    data class Italic(val children: List<MarkdownInline>) : MarkdownInline

    data class Code(val code: String) : MarkdownInline

    data class Link(
        val children: List<MarkdownInline>,
        val url: String,
    ) : MarkdownInline
}

/**
 * True for list items. Consecutive list items are laid out closer together than other blocks.
 */
val MarkdownBlock.isListItem: Boolean
    get() = this is MarkdownBlock.BulletItem ||
        this is MarkdownBlock.NumberedItem ||
        this is MarkdownBlock.ChecklistItem
