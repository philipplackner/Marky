package com.plcoding.marky.core.markdown

import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.flavours.gfm.GFMTokenTypes

enum class SyntaxStyle {
    /** Markdown characters such as `#`, `**`, `- [ ] ` or `](`. */
    Syntax,
    Heading1,
    Heading2,
    Heading3,
    Bold,
    Italic,
    Code,
    /** The whole `[text](url)` link, including its syntax. */
    Link,
    /** The URL part of a link, or a bare URL. */
    Url,
}

/**
 * A range of the raw text that Write mode shows in [style].
 *
 * @property end Exclusive.
 */
data class SyntaxSpan(
    val style: SyntaxStyle,
    val start: Int,
    val end: Int,
)

/**
 * Finds the formatted ranges of raw Markdown [text] for Write mode.
 *
 * Outer spans come before the spans inside them, so applying them in order lets the
 * syntax style win over the style of its node.
 */
fun markdownSyntaxSpans(text: String): List<SyntaxSpan> {
    val spans = mutableListOf<SyntaxSpan>()
    collectSpans(parseTree(text), spans)
    return spans
}

private fun collectSpans(node: ASTNode, spans: MutableList<SyntaxSpan>) {
    fun add(style: SyntaxStyle, from: ASTNode = node) {
        if (from.endOffset > from.startOffset) {
            spans += SyntaxSpan(style, from.startOffset, from.endOffset)
        }
    }

    fun recurse(children: List<ASTNode> = node.children) = children.forEach { collectSpans(it, spans) }

    when (node.type) {
        MarkdownElementTypes.ATX_1 -> heading(node, SyntaxStyle.Heading1, spans)
        MarkdownElementTypes.ATX_2 -> heading(node, SyntaxStyle.Heading2, spans)
        MarkdownElementTypes.ATX_3 -> heading(node, SyntaxStyle.Heading3, spans)
        MarkdownElementTypes.STRONG -> {
            add(SyntaxStyle.Bold)
            node.children.take(2).forEach { add(SyntaxStyle.Syntax, it) }
            recurse(node.children.drop(2).dropLast(2))
            node.children.takeLast(2).forEach { add(SyntaxStyle.Syntax, it) }
        }
        MarkdownElementTypes.EMPH -> if (node.children.isNotEmpty()) {
            add(SyntaxStyle.Italic)
            add(SyntaxStyle.Syntax, node.children.first())
            recurse(node.children.drop(1).dropLast(1))
            add(SyntaxStyle.Syntax, node.children.last())
        }
        MarkdownElementTypes.CODE_SPAN -> {
            add(SyntaxStyle.Code)
            node.children.filter { it.type == MarkdownTokenTypes.BACKTICK }.forEach { add(SyntaxStyle.Syntax, it) }
        }
        MarkdownElementTypes.CODE_FENCE -> {
            add(SyntaxStyle.Code)
            node.children
                .filter { it.type in FenceSyntax }
                .forEach { add(SyntaxStyle.Syntax, it) }
        }
        MarkdownElementTypes.CODE_BLOCK -> add(SyntaxStyle.Code)
        MarkdownElementTypes.INLINE_LINK -> {
            add(SyntaxStyle.Link)
            for (child in node.children) {
                when (child.type) {
                    MarkdownElementTypes.LINK_TEXT -> {
                        add(SyntaxStyle.Syntax, child.children.first())
                        recurse(child.children.drop(1).dropLast(1))
                        add(SyntaxStyle.Syntax, child.children.last())
                    }
                    MarkdownElementTypes.LINK_DESTINATION, MarkdownElementTypes.LINK_TITLE -> add(SyntaxStyle.Url, child)
                    MarkdownTokenTypes.LPAREN, MarkdownTokenTypes.RPAREN -> add(SyntaxStyle.Syntax, child)
                    else -> Unit
                }
            }
        }
        MarkdownElementTypes.AUTOLINK -> {
            add(SyntaxStyle.Url)
            node.children
                .filter { it.type == MarkdownTokenTypes.LT || it.type == MarkdownTokenTypes.GT }
                .forEach { add(SyntaxStyle.Syntax, it) }
        }
        GFMTokenTypes.GFM_AUTOLINK -> add(SyntaxStyle.Url)
        MarkdownTokenTypes.LIST_BULLET,
        MarkdownTokenTypes.LIST_NUMBER,
        GFMTokenTypes.CHECK_BOX,
        MarkdownTokenTypes.BLOCK_QUOTE -> add(SyntaxStyle.Syntax)
        MarkdownElementTypes.BLOCK_QUOTE -> if (node.children.isEmpty()) add(SyntaxStyle.Syntax) else recurse()
        else -> recurse()
    }
}

private fun heading(node: ASTNode, style: SyntaxStyle, spans: MutableList<SyntaxSpan>) {
    spans += SyntaxSpan(style, node.startOffset, node.endOffset)
    node.children.forEach { child ->
        if (child.type == MarkdownTokenTypes.ATX_HEADER) {
            spans += SyntaxSpan(SyntaxStyle.Syntax, child.startOffset, child.endOffset)
        } else {
            collectSpans(child, spans)
        }
    }
}

private val FenceSyntax = setOf(
    MarkdownTokenTypes.CODE_FENCE_START,
    MarkdownTokenTypes.CODE_FENCE_END,
    MarkdownTokenTypes.FENCE_LANG,
)
