package com.plcoding.marky.core.markdown

import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.flavours.gfm.GFMTokenTypes
import org.intellij.markdown.parser.CancellationToken
import org.intellij.markdown.parser.MarkdownParser

/**
 * Parses [text] as GitHub-flavoured Markdown and maps it to Marky's [MarkdownDocument].
 */
fun parseMarkdown(text: String): MarkdownDocument {
    val root = parseTree(text)
    return MarkdownDocument(DocumentBuilder(text).blocksOf(root.children))
}

internal fun parseTree(text: String): ASTNode =
    MarkdownParser(GfmFlavour, cancellationToken = CancellationToken.NonCancellable)
        .buildMarkdownTreeFromString(text as CharSequence)

private val GfmFlavour = GFMFlavourDescriptor()

private class DocumentBuilder(private val source: String) {

    fun blocksOf(nodes: List<ASTNode>): List<MarkdownBlock> = nodes.flatMap(::blocksOf)

    private fun blocksOf(node: ASTNode): List<MarkdownBlock> = when (node.type) {
        MarkdownElementTypes.ATX_1 -> listOf(heading(node, 1))
        MarkdownElementTypes.ATX_2 -> listOf(heading(node, 2))
        MarkdownElementTypes.ATX_3 -> listOf(heading(node, 3))
        MarkdownElementTypes.SETEXT_1 -> listOf(heading(node, 1))
        MarkdownElementTypes.SETEXT_2 -> listOf(heading(node, 2))
        MarkdownElementTypes.PARAGRAPH -> listOf(MarkdownBlock.Paragraph(inlinesOf(node.children).trimmed()))
        MarkdownElementTypes.UNORDERED_LIST -> listItems(node, ordered = false)
        MarkdownElementTypes.ORDERED_LIST -> listItems(node, ordered = true)
        MarkdownElementTypes.BLOCK_QUOTE -> listOf(MarkdownBlock.Quote(quoteContent(node).trimmed()))
        MarkdownElementTypes.CODE_FENCE -> listOf(MarkdownBlock.CodeBlock(fenceCode(node)))
        MarkdownElementTypes.CODE_BLOCK -> listOf(MarkdownBlock.CodeBlock(indentedCode(node)))
        MarkdownTokenTypes.EOL, MarkdownTokenTypes.WHITE_SPACE -> emptyList()
        else -> {
            val raw = node.text().trim()
            if (raw.isEmpty()) emptyList() else listOf(MarkdownBlock.Paragraph(listOf(MarkdownInline.Text(raw))))
        }
    }

    private fun heading(node: ASTNode, level: Int): MarkdownBlock.Heading {
        val content = node.children.firstOrNull {
            it.type == MarkdownTokenTypes.ATX_CONTENT || it.type == MarkdownTokenTypes.SETEXT_CONTENT
        }
        return MarkdownBlock.Heading(
            level = level,
            content = content?.let { inlinesOf(it.children).trimmed() }.orEmpty(),
        )
    }

    private fun listItems(list: ASTNode, ordered: Boolean): List<MarkdownBlock> {
        val blocks = mutableListOf<MarkdownBlock>()
        var number: Int? = null
        for (item in list.children.filter { it.type == MarkdownElementTypes.LIST_ITEM }) {
            if (ordered) {
                number = number?.plus(1) ?: item.children
                    .firstOrNull { it.type == MarkdownTokenTypes.LIST_NUMBER }
                    ?.text()
                    ?.takeWhile { it.isDigit() }
                    ?.toIntOrNull()
                    ?: 1
            }
            val paragraphs = item.children.filter { it.type == MarkdownElementTypes.PARAGRAPH }
            val content = joinLines(paragraphs.map { inlinesOf(it.children) }).trimmed()
            val checkbox = item.children.firstOrNull { it.type == GFMTokenTypes.CHECK_BOX }
            blocks += when {
                checkbox != null -> MarkdownBlock.ChecklistItem(
                    checked = checkbox.text().getOrNull(1)?.lowercaseChar() == 'x',
                    checkboxOffset = checkbox.startOffset,
                    content = content,
                )
                ordered -> MarkdownBlock.NumberedItem(number = number ?: 1, content = content)
                else -> MarkdownBlock.BulletItem(content)
            }
            // Read mode has no nested lists, so nested blocks follow their parent item.
            val nested = item.children.filter {
                it.type != MarkdownElementTypes.PARAGRAPH && it.type !in ListItemMarkers
            }
            blocks += blocksOf(nested)
        }
        return blocks
    }

    private fun quoteContent(quote: ASTNode): List<MarkdownInline> {
        val parts = quote.children.mapNotNull { child ->
            when {
                child.type == MarkdownElementTypes.PARAGRAPH -> inlinesOf(child.children)
                child.type == MarkdownElementTypes.BLOCK_QUOTE && child.children.isNotEmpty() -> quoteContent(child)
                child.type in Separators || child.isQuoteMarker() -> null
                else -> child.text().trim().takeIf { it.isNotEmpty() }?.let { listOf(MarkdownInline.Text(it)) }
            }
        }
        return joinLines(parts)
    }

    private fun fenceCode(fence: ASTNode): String {
        val content = fence.children.filter { it.type == MarkdownTokenTypes.CODE_FENCE_CONTENT }
        if (content.isEmpty()) return ""
        return source.substring(content.first().startOffset, content.last().endOffset)
    }

    private fun indentedCode(block: ASTNode): String =
        block.text().lines().joinToString("\n") { line ->
            line.removePrefix("    ").removePrefix("\t")
        }.trimEnd()

    private fun inlinesOf(nodes: List<ASTNode>): List<MarkdownInline> {
        val inlines = mutableListOf<MarkdownInline>()
        var lineStart = false
        for (node in nodes) {
            if (lineStart && (node.type == MarkdownTokenTypes.WHITE_SPACE || node.isQuoteMarker())) {
                continue
            }
            lineStart = false
            when (node.type) {
                MarkdownTokenTypes.EOL -> {
                    inlines += MarkdownInline.Text("\n")
                    lineStart = true
                }
                MarkdownElementTypes.STRONG ->
                    inlines += MarkdownInline.Bold(inlinesOf(node.children.drop(2).dropLast(2)))
                MarkdownElementTypes.EMPH ->
                    inlines += MarkdownInline.Italic(inlinesOf(node.children.drop(1).dropLast(1)))
                MarkdownElementTypes.CODE_SPAN -> inlines += MarkdownInline.Code(codeSpan(node))
                MarkdownElementTypes.INLINE_LINK -> inlines += inlineLink(node)
                MarkdownElementTypes.AUTOLINK -> {
                    val url = node.children.firstOrNull { it.type == MarkdownTokenTypes.AUTOLINK }?.text().orEmpty()
                    inlines += MarkdownInline.Link(
                        children = listOf(MarkdownInline.Text(url)),
                        url = if (':' in url) url else "mailto:$url",
                    )
                }
                GFMTokenTypes.GFM_AUTOLINK -> {
                    val url = node.text()
                    inlines += MarkdownInline.Link(
                        children = listOf(MarkdownInline.Text(url)),
                        url = if ("://" in url) url else "https://$url",
                    )
                }
                else -> inlines += MarkdownInline.Text(node.text())
            }
        }
        return inlines.mergeText()
    }

    private fun inlineLink(link: ASTNode): MarkdownInline {
        val label = link.children.firstOrNull { it.type == MarkdownElementTypes.LINK_TEXT }
        val destination = link.children.firstOrNull { it.type == MarkdownElementTypes.LINK_DESTINATION }
        return MarkdownInline.Link(
            children = label?.let { inlinesOf(it.children.drop(1).dropLast(1)) }.orEmpty(),
            url = destination?.text()?.removeSurrounding("<", ">").orEmpty(),
        )
    }

    private fun codeSpan(span: ASTNode): String {
        val inner = span.children
            .dropWhile { it.type == MarkdownTokenTypes.BACKTICK }
            .dropLastWhile { it.type == MarkdownTokenTypes.BACKTICK }
        if (inner.isEmpty()) return ""
        val code = source.substring(inner.first().startOffset, inner.last().endOffset).replace('\n', ' ')
        // CommonMark strips one space on each side so code can start or end with a backtick.
        return if (code.length >= 2 && code.first() == ' ' && code.last() == ' ' && code.isNotBlank()) {
            code.substring(1, code.length - 1)
        } else {
            code
        }
    }

    private fun ASTNode.text(): String = source.substring(startOffset, endOffset)

    private fun ASTNode.isQuoteMarker(): Boolean =
        type == MarkdownTokenTypes.BLOCK_QUOTE ||
            (type == MarkdownElementTypes.BLOCK_QUOTE && children.isEmpty())

    private companion object {
        val ListItemMarkers: Set<IElementType> = setOf(
            MarkdownTokenTypes.LIST_BULLET,
            MarkdownTokenTypes.LIST_NUMBER,
            GFMTokenTypes.CHECK_BOX,
            MarkdownTokenTypes.EOL,
            MarkdownTokenTypes.WHITE_SPACE,
        )
        val Separators: Set<IElementType> = setOf(MarkdownTokenTypes.EOL, MarkdownTokenTypes.WHITE_SPACE)
    }
}

private fun joinLines(lines: List<List<MarkdownInline>>): List<MarkdownInline> =
    lines.flatMapIndexed { index, line ->
        if (index == 0) line else listOf(MarkdownInline.Text("\n")) + line
    }.mergeText()

private fun List<MarkdownInline>.mergeText(): List<MarkdownInline> {
    val merged = mutableListOf<MarkdownInline>()
    for (inline in this) {
        val last = merged.lastOrNull()
        if (inline is MarkdownInline.Text && last is MarkdownInline.Text) {
            merged[merged.lastIndex] = MarkdownInline.Text(last.text + inline.text)
        } else {
            merged += inline
        }
    }
    return merged
}

private fun List<MarkdownInline>.trimmed(): List<MarkdownInline> {
    if (isEmpty()) return this
    val result = toMutableList()
    (result.first() as? MarkdownInline.Text)?.let { result[0] = MarkdownInline.Text(it.text.trimStart()) }
    (result.last() as? MarkdownInline.Text)?.let { result[result.lastIndex] = MarkdownInline.Text(it.text.trimEnd()) }
    return result.filterNot { it is MarkdownInline.Text && it.text.isEmpty() }
}
