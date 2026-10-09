package com.plcoding.marky.core.presentation.markdown

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.plcoding.marky.core.designsystem.markdown.MarkdownBulletItem
import com.plcoding.marky.core.designsystem.markdown.MarkdownChecklistItem
import com.plcoding.marky.core.designsystem.markdown.MarkdownCodeBlock
import com.plcoding.marky.core.designsystem.markdown.MarkdownHeading
import com.plcoding.marky.core.designsystem.markdown.MarkdownInlineStyles
import com.plcoding.marky.core.designsystem.markdown.MarkdownNumberedItem
import com.plcoding.marky.core.designsystem.markdown.MarkdownParagraph
import com.plcoding.marky.core.designsystem.markdown.MarkdownQuote
import com.plcoding.marky.core.designsystem.markdown.MarkdownSpacing
import com.plcoding.marky.core.designsystem.markdown.appendInlineCode
import com.plcoding.marky.core.designsystem.markdown.appendLink
import com.plcoding.marky.core.designsystem.markdown.rememberMarkdownInlineStyles
import com.plcoding.marky.core.designsystem.theme.MarkyTheme
import com.plcoding.marky.core.markdown.MarkdownBlock
import com.plcoding.marky.core.markdown.MarkdownDocument
import com.plcoding.marky.core.markdown.MarkdownInline
import com.plcoding.marky.core.markdown.isListItem
import com.plcoding.marky.core.markdown.parseMarkdown
import com.plcoding.marky.core.markdown.plainText

/**
 * Read mode of a note: renders [document] with the design system's Markdown nodes.
 * Links open in the system browser.
 *
 * @param onCheckboxToggle called with the item's checkbox offset in the source text
 * when a checklist item is ticked or unticked.
 */
@Composable
fun MarkdownDocumentView(
    document: MarkdownDocument,
    onCheckboxToggle: (checkboxOffset: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val styles = rememberMarkdownInlineStyles()
    val uriHandler = LocalUriHandler.current
    val safeUriHandler = remember(uriHandler) { SafeUriHandler(uriHandler) }
    CompositionLocalProvider(LocalUriHandler provides safeUriHandler) {
        MarkdownBlocks(
            blocks = document.blocks,
            level = 1,
            styles = styles,
            onCheckboxToggle = onCheckboxToggle,
            modifier = modifier,
        )
    }
}

/**
 * @param level Nesting level of the lists among [blocks], starting at 1.
 */
@Composable
private fun MarkdownBlocks(
    blocks: List<MarkdownBlock>,
    level: Int,
    styles: MarkdownInlineStyles,
    onCheckboxToggle: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        blocks.forEachIndexed { index, block ->
            if (index > 0) {
                Spacer(modifier = Modifier.height(spacingBefore(block, blocks[index - 1])))
            }
            MarkdownBlockView(block = block, level = level, styles = styles, onCheckboxToggle = onCheckboxToggle)
        }
    }
}

@Composable
private fun MarkdownBlockView(
    block: MarkdownBlock,
    level: Int,
    styles: MarkdownInlineStyles,
    onCheckboxToggle: (Int) -> Unit,
) {
    val nested: (@Composable () -> Unit)? = block.listChildren.takeIf { it.isNotEmpty() }?.let { children ->
        {
            MarkdownBlocks(
                blocks = children,
                level = level + 1,
                styles = styles,
                onCheckboxToggle = onCheckboxToggle,
            )
        }
    }
    when (block) {
        is MarkdownBlock.Heading -> MarkdownHeading(text = block.content.toAnnotatedString(styles), level = block.level)
        is MarkdownBlock.Paragraph -> MarkdownParagraph(text = block.content.toAnnotatedString(styles))
        is MarkdownBlock.BulletItem -> MarkdownBulletItem(
            text = block.content.toAnnotatedString(styles),
            level = level,
            nested = nested,
        )
        is MarkdownBlock.NumberedItem -> MarkdownNumberedItem(
            number = block.number,
            text = block.content.toAnnotatedString(styles),
            level = level,
            nested = nested,
        )
        is MarkdownBlock.ChecklistItem -> MarkdownChecklistItem(
            checked = block.checked,
            onCheckedChange = { onCheckboxToggle(block.checkboxOffset) },
            text = block.content.toAnnotatedString(styles),
            level = level,
            nested = nested,
        )
        is MarkdownBlock.Quote -> MarkdownQuote {
            MarkdownBlocks(
                blocks = block.children,
                level = level,
                styles = styles,
                onCheckboxToggle = onCheckboxToggle,
            )
        }
        is MarkdownBlock.CodeBlock -> MarkdownCodeBlock(code = block.code)
    }
}

private val MarkdownBlock.listChildren: List<MarkdownBlock>
    get() = when (this) {
        is MarkdownBlock.BulletItem -> children
        is MarkdownBlock.NumberedItem -> children
        is MarkdownBlock.ChecklistItem -> children
        else -> emptyList()
    }

private fun spacingBefore(block: MarkdownBlock, previous: MarkdownBlock): Dp = when {
    block is MarkdownBlock.Heading -> when (block.level) {
        1 -> MarkdownSpacing.beforeHeading1
        2 -> MarkdownSpacing.beforeHeading2
        else -> MarkdownSpacing.beforeHeading3
    }
    // Checklist items carry their own vertical padding, so they sit flush.
    block is MarkdownBlock.ChecklistItem && previous is MarkdownBlock.ChecklistItem -> 0.dp
    block.isListItem && block::class == previous::class -> MarkdownSpacing.listItem
    else -> MarkdownSpacing.block
}

private fun List<MarkdownInline>.toAnnotatedString(styles: MarkdownInlineStyles): AnnotatedString =
    buildAnnotatedString { appendInlines(this@toAnnotatedString, styles) }

private fun AnnotatedString.Builder.appendInlines(inlines: List<MarkdownInline>, styles: MarkdownInlineStyles) {
    for (inline in inlines) {
        when (inline) {
            is MarkdownInline.Text -> append(inline.text)
            is MarkdownInline.Bold -> withStyle(styles.bold) { appendInlines(inline.children, styles) }
            is MarkdownInline.Italic -> withStyle(styles.italic) { appendInlines(inline.children, styles) }
            is MarkdownInline.Code -> appendInlineCode(inline.code)
            is MarkdownInline.Link -> appendLink(
                text = plainText(inline.children),
                url = inline.url,
                styles = styles,
            )
        }
    }
}

/**
 * Links come from user text, so a malformed URL or one no app can open must not crash the app.
 */
private class SafeUriHandler(private val delegate: UriHandler) : UriHandler {
    override fun openUri(uri: String) {
        try {
            delegate.openUri(uri)
        } catch (_: Exception) {
        }
    }
}

private val WelcomeNote = """
    |Marky keeps notes as **Markdown**: plain text with a few symbols for formatting. Tap *Write* to see them.
    |
    |# Heading 1
    |## Heading 2
    |### Heading 3
    |
    |Text can be **bold**, *italic* or `inline code`.
    |
    |- A bullet list
    |- Pin notes you open often
    |
    |1. A numbered list
    |2. Export any note as .md
    |
    |- [x] Open this note
    |- [ ] Tick this box
    |
    |> Formatting lives in the text, so your notes work in any editor.
    |
    |```
    |code blocks keep
    |  their spacing
    |```
    |
    |[A link opens in the browser](https://example.com)
""".trimMargin()

private val NestedListsNote = """
    |## Lisbon plan
    |
    |- Food
    |  - Pastéis de nata
    |    - Manteigaria
    |      - Level 4 stops indenting
    |    - Pastéis de Belém
    |  - Grilled sardines
    |- Sights
    |
    |1. Check in
    |   1. Print boarding pass
    |   2. Drop bags
    |2. Security
    |   - Liquids in one bag
    |
    |- [x] Pack
    |  - [x] Passport
    |  - [ ] Sunscreen
    |- [ ] Book tram
    |  - Line 28 from Martim Moniz
""".trimMargin()

private val QuoteNote = """
    |> Go **early**, it's packed by noon.
    |> It's worth the queue.
    |
    |> Before you go:
    |>
    |> - [x] Passport
    |> - [ ] Sunscreen
    |>   - Factor 50
    |>
    |> 1. Check in
    |> 2. Security
    |>
    |> ### Flight
    |>
    |> ```
    |> TP 1353  Fri 08:40
    |> ```
    |>
    |> > Nested quote
""".trimMargin()

@Composable
private fun DocumentPreview(darkTheme: Boolean, markdown: String = WelcomeNote) {
    MarkyTheme(darkTheme = darkTheme) {
        Box(modifier = Modifier.background(MarkyTheme.colors.background)) {
            MarkdownDocumentView(
                document = parseMarkdown(markdown),
                onCheckboxToggle = {},
                modifier = Modifier.padding(MarkdownSpacing.horizontal),
            )
        }
    }
}

@Preview(name = "All nodes", heightDp = 1100)
@Composable
private fun MarkdownDocumentViewPreview() {
    DocumentPreview(darkTheme = false)
}

@Preview(name = "All nodes, dark", heightDp = 1100)
@Composable
private fun MarkdownDocumentViewDarkPreview() {
    DocumentPreview(darkTheme = true)
}

@Preview(name = "Nested lists", heightDp = 700)
@Composable
private fun MarkdownDocumentViewNestedPreview() {
    DocumentPreview(darkTheme = false, markdown = NestedListsNote)
}

@Preview(name = "Nested lists, dark", heightDp = 700)
@Composable
private fun MarkdownDocumentViewNestedDarkPreview() {
    DocumentPreview(darkTheme = true, markdown = NestedListsNote)
}

@Preview(name = "Blocks in quotes", heightDp = 700)
@Composable
private fun MarkdownDocumentViewQuotePreview() {
    DocumentPreview(darkTheme = false, markdown = QuoteNote)
}

@Preview(name = "Blocks in quotes, dark", heightDp = 700)
@Composable
private fun MarkdownDocumentViewQuoteDarkPreview() {
    DocumentPreview(darkTheme = true, markdown = QuoteNote)
}
