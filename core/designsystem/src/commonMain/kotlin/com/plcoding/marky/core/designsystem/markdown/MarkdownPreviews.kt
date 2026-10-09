package com.plcoding.marky.core.designsystem.markdown

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.plcoding.marky.core.designsystem.components.text.MarkyText
import com.plcoding.marky.core.designsystem.preview.MarkyPreview

private val PreviewWidth = 340.dp

@Composable
private fun Headings() {
    Column(modifier = Modifier.width(PreviewWidth)) {
        MarkdownHeading(text = AnnotatedString("Lisbon"), level = 1)
        Spacer(modifier = Modifier.height(MarkdownSpacing.beforeHeading2))
        MarkdownHeading(text = AnnotatedString("Packing"), level = 2)
        Spacer(modifier = Modifier.height(MarkdownSpacing.beforeHeading3))
        MarkdownHeading(text = AnnotatedString("Day one"), level = 3)
        Spacer(modifier = Modifier.height(MarkdownSpacing.beforeHeading2))
        MarkdownHeading(text = AnnotatedString("A second-level heading that wraps onto two lines"), level = 2)
    }
}

@Composable
private fun Inline() {
    val styles = rememberMarkdownInlineStyles()
    Column(
        modifier = Modifier.width(PreviewWidth),
        verticalArrangement = Arrangement.spacedBy(MarkdownSpacing.block),
    ) {
        MarkdownParagraph(
            text = buildAnnotatedString {
                append("Flights are ")
                withStyle(styles.bold) { append("booked") }
                append(", the ")
                withStyle(styles.italic) { append("tram pass") }
                append(" isn't.")
            },
        )
        MarkdownParagraph(
            text = buildAnnotatedString {
                append("Booking ref ")
                appendInlineCode("X7QK2")
            },
        )
        MarkdownParagraph(
            text = buildAnnotatedString {
                appendLink(text = "Tram 28 route", url = "https://carris.pt", styles = styles)
            },
        )
    }
}

@Composable
private fun Lists() {
    Column(modifier = Modifier.width(PreviewWidth)) {
        MarkdownBulletItem(text = AnnotatedString("Pastéis de nata"))
        Spacer(modifier = Modifier.height(MarkdownSpacing.listItem))
        MarkdownBulletItem(text = AnnotatedString("Sardines, grilled on the street, with bread and a cold drink"))
        Spacer(modifier = Modifier.height(MarkdownSpacing.block))
        MarkdownNumberedItem(number = 1, text = AnnotatedString("Check in"))
        Spacer(modifier = Modifier.height(MarkdownSpacing.listItem))
        MarkdownNumberedItem(number = 2, text = AnnotatedString("Drop bags"))
    }
}

@Composable
private fun Checklist() {
    Column(modifier = Modifier.width(PreviewWidth)) {
        MarkdownChecklistItem(checked = true, onCheckedChange = {}, text = AnnotatedString("Passport"))
        MarkdownChecklistItem(checked = false, onCheckedChange = {}, text = AnnotatedString("Sunscreen"))
    }
}

@Composable
private fun NestedLists() {
    Column(modifier = Modifier.width(PreviewWidth)) {
        MarkdownBulletItem(
            text = AnnotatedString("Food"),
            nested = {
                Column {
                    MarkdownBulletItem(
                        text = AnnotatedString("Pastéis de nata"),
                        level = 2,
                        nested = {
                            Column {
                                MarkdownBulletItem(
                                    text = AnnotatedString("Manteigaria"),
                                    level = 3,
                                    nested = {
                                        MarkdownBulletItem(text = AnnotatedString("Level 4 stops indenting"), level = 4)
                                    },
                                )
                                Spacer(modifier = Modifier.height(MarkdownSpacing.listItem))
                                MarkdownBulletItem(text = AnnotatedString("Pastéis de Belém"), level = 3)
                            }
                        },
                    )
                    Spacer(modifier = Modifier.height(MarkdownSpacing.listItem))
                    MarkdownBulletItem(text = AnnotatedString("Grilled sardines"), level = 2)
                }
            },
        )
        Spacer(modifier = Modifier.height(MarkdownSpacing.listItem))
        MarkdownBulletItem(text = AnnotatedString("Sights"))
        Spacer(modifier = Modifier.height(MarkdownSpacing.block))
        MarkdownNumberedItem(
            number = 1,
            text = AnnotatedString("Check in"),
            nested = {
                Column {
                    MarkdownNumberedItem(number = 1, text = AnnotatedString("Print boarding pass"), level = 2)
                    Spacer(modifier = Modifier.height(MarkdownSpacing.listItem))
                    MarkdownNumberedItem(number = 2, text = AnnotatedString("Drop bags"), level = 2)
                }
            },
        )
        Spacer(modifier = Modifier.height(MarkdownSpacing.listItem))
        MarkdownNumberedItem(
            number = 2,
            text = AnnotatedString("Security"),
            nested = { MarkdownBulletItem(text = AnnotatedString("Liquids in one bag"), level = 2) },
        )
        Spacer(modifier = Modifier.height(MarkdownSpacing.block))
        MarkdownChecklistItem(
            checked = true,
            onCheckedChange = {},
            text = AnnotatedString("Pack"),
            nested = {
                Column {
                    MarkdownChecklistItem(checked = true, onCheckedChange = {}, text = AnnotatedString("Passport"), level = 2)
                    MarkdownChecklistItem(checked = false, onCheckedChange = {}, text = AnnotatedString("Sunscreen"), level = 2)
                }
            },
        )
        MarkdownChecklistItem(
            checked = false,
            onCheckedChange = {},
            text = AnnotatedString("Book tram"),
            nested = { MarkdownBulletItem(text = AnnotatedString("Line 28 from Martim Moniz"), level = 2) },
        )
    }
}

@Composable
private fun QuoteAndCode() {
    Column(
        modifier = Modifier.width(PreviewWidth),
        verticalArrangement = Arrangement.spacedBy(MarkdownSpacing.block),
    ) {
        MarkdownQuote { MarkdownParagraph(text = AnnotatedString("Go early, it's packed by noon.")) }
        MarkdownCodeBlock(code = "TP 1353  Fri 08:40\nSeat 14C  Gate 07:55  -> Terminal 1, a long line that is cut off")
    }
}

@Composable
private fun QuoteWithBlocks() {
    MarkdownQuote(modifier = Modifier.width(PreviewWidth)) {
        Column {
            MarkdownParagraph(text = AnnotatedString("Before you go:"))
            Spacer(modifier = Modifier.height(MarkdownSpacing.block))
            MarkdownChecklistItem(checked = true, onCheckedChange = {}, text = AnnotatedString("Passport"))
            MarkdownChecklistItem(checked = false, onCheckedChange = {}, text = AnnotatedString("Sunscreen"))
            Spacer(modifier = Modifier.height(MarkdownSpacing.block))
            MarkdownBulletItem(text = AnnotatedString("Line 28 from Martim Moniz"))
            Spacer(modifier = Modifier.height(MarkdownSpacing.listItem))
            MarkdownNumberedItem(number = 1, text = AnnotatedString("Check in"))
            Spacer(modifier = Modifier.height(MarkdownSpacing.beforeHeading3))
            MarkdownHeading(text = AnnotatedString("Flight"), level = 3)
            Spacer(modifier = Modifier.height(MarkdownSpacing.block))
            MarkdownCodeBlock(code = "TP 1353  Fri 08:40")
        }
    }
}

@Composable
private fun AllNodes() {
    val styles = rememberMarkdownInlineStyles()
    Column(modifier = Modifier.width(PreviewWidth)) {
        MarkdownHeading(text = AnnotatedString("Welcome to Marky"), level = 1)
        Spacer(modifier = Modifier.height(MarkdownSpacing.block))
        MarkdownParagraph(
            text = buildAnnotatedString {
                append("Write in ")
                withStyle(styles.bold) { append("Markdown") }
                append(", read it ")
                withStyle(styles.italic) { append("formatted") }
                append(". Use ")
                appendInlineCode("Write")
                append(" and ")
                appendInlineCode("Read")
                append(" to switch.")
            },
        )
        Spacer(modifier = Modifier.height(MarkdownSpacing.beforeHeading2))
        MarkdownHeading(text = AnnotatedString("Things to try"), level = 2)
        Spacer(modifier = Modifier.height(MarkdownSpacing.block))
        MarkdownChecklistItem(checked = true, onCheckedChange = {}, text = AnnotatedString("Open this note"))
        MarkdownChecklistItem(checked = false, onCheckedChange = {}, text = AnnotatedString("Tick this box"))
        Spacer(modifier = Modifier.height(MarkdownSpacing.block))
        MarkdownBulletItem(text = AnnotatedString("Pin notes you open often"))
        Spacer(modifier = Modifier.height(MarkdownSpacing.listItem))
        MarkdownBulletItem(text = AnnotatedString("Export any note as .md"))
        Spacer(modifier = Modifier.height(MarkdownSpacing.block))
        MarkdownQuote { MarkdownParagraph(text = AnnotatedString("Formatting lives in the text.")) }
        Spacer(modifier = Modifier.height(MarkdownSpacing.block))
        MarkdownCodeBlock(code = "# Heading\n**bold** and *italic*")
        Spacer(modifier = Modifier.height(MarkdownSpacing.block))
        MarkdownParagraph(
            text = buildAnnotatedString {
                appendLink(text = "Markdown guide", url = "https://commonmark.org/help/", styles = styles)
            },
        )
    }
}

@Composable
private fun EditorStyles() {
    val styles = rememberMarkdownEditorStyles()
    val lines: List<AnnotatedString> = listOf(
        buildAnnotatedString {
            withStyle(styles.syntax) { append("# ") }
            withStyle(styles.heading1) { append("Lisbon") }
        },
        buildAnnotatedString {
            withStyle(styles.syntax) { append("## ") }
            withStyle(styles.heading2) { append("Packing") }
        },
        buildAnnotatedString {
            withStyle(styles.syntax) { append("### ") }
            withStyle(styles.heading3) { append("Day one") }
        },
        buildAnnotatedString {
            append("Flights are ")
            withStyle(styles.syntax) { append("**") }
            withStyle(styles.bold) { append("booked") }
            withStyle(styles.syntax) { append("**") }
            append(", the ")
            withStyle(styles.syntax) { append("*") }
            withStyle(styles.italic) { append("tram pass") }
            withStyle(styles.syntax) { append("*") }
            append(" isn't.")
        },
        buildAnnotatedString {
            append("Booking ref ")
            withStyle(styles.syntax) { append("`") }
            withStyle(styles.code) { append("X7QK2") }
            withStyle(styles.syntax) { append("`") }
        },
        buildAnnotatedString {
            withStyle(styles.syntax) { append("- [x] ") }
            append("Passport\n")
            withStyle(styles.syntax) { append("- [ ] ") }
            append("Sunscreen")
        },
        buildAnnotatedString {
            withStyle(styles.syntax) { append("> ") }
            append("Go early, it's packed by noon.")
        },
        buildAnnotatedString {
            withStyle(styles.syntax) { append("[") }
            append("Tram 28 route")
            withStyle(styles.syntax) { append("](") }
            withStyle(styles.url) { append("carris.pt") }
            withStyle(styles.syntax) { append(")") }
        },
        buildAnnotatedString {
            withStyle(styles.placeholder) { append("Start writing…") }
        },
    )
    Column(
        modifier = Modifier.width(PreviewWidth),
        verticalArrangement = Arrangement.spacedBy(MarkdownSpacing.block),
    ) {
        lines.forEach { line -> MarkyText(text = line, style = styles.text) }
    }
}

@Preview(name = "Headings · light")
@Composable
private fun HeadingsLightPreview() {
    MarkyPreview(padding = 20.dp) { Headings() }
}

@Preview(name = "Headings · dark")
@Composable
private fun HeadingsDarkPreview() {
    MarkyPreview(darkTheme = true, padding = 20.dp) { Headings() }
}

@Preview(name = "Inline · light")
@Composable
private fun InlineLightPreview() {
    MarkyPreview(padding = 20.dp) { Inline() }
}

@Preview(name = "Inline · dark")
@Composable
private fun InlineDarkPreview() {
    MarkyPreview(darkTheme = true, padding = 20.dp) { Inline() }
}

@Preview(name = "Lists · light")
@Composable
private fun ListsLightPreview() {
    MarkyPreview(padding = 20.dp) { Lists() }
}

@Preview(name = "Lists · dark")
@Composable
private fun ListsDarkPreview() {
    MarkyPreview(darkTheme = true, padding = 20.dp) { Lists() }
}

@Preview(name = "Checklist · light")
@Composable
private fun ChecklistLightPreview() {
    MarkyPreview(padding = 20.dp) { Checklist() }
}

@Preview(name = "Checklist · dark")
@Composable
private fun ChecklistDarkPreview() {
    MarkyPreview(darkTheme = true, padding = 20.dp) { Checklist() }
}

@Preview(name = "Nested lists · light")
@Composable
private fun NestedListsLightPreview() {
    MarkyPreview(padding = 20.dp) { NestedLists() }
}

@Preview(name = "Nested lists · dark")
@Composable
private fun NestedListsDarkPreview() {
    MarkyPreview(darkTheme = true, padding = 20.dp) { NestedLists() }
}

@Preview(name = "Quote and code · light")
@Composable
private fun QuoteAndCodeLightPreview() {
    MarkyPreview(padding = 20.dp) { QuoteAndCode() }
}

@Preview(name = "Quote and code · dark")
@Composable
private fun QuoteAndCodeDarkPreview() {
    MarkyPreview(darkTheme = true, padding = 20.dp) { QuoteAndCode() }
}

@Preview(name = "Quote with blocks · light")
@Composable
private fun QuoteWithBlocksLightPreview() {
    MarkyPreview(padding = 20.dp) { QuoteWithBlocks() }
}

@Preview(name = "Quote with blocks · dark")
@Composable
private fun QuoteWithBlocksDarkPreview() {
    MarkyPreview(darkTheme = true, padding = 20.dp) { QuoteWithBlocks() }
}

@Preview(name = "All nodes · light")
@Composable
private fun AllNodesLightPreview() {
    MarkyPreview(padding = 20.dp) { AllNodes() }
}

@Preview(name = "All nodes · dark")
@Composable
private fun AllNodesDarkPreview() {
    MarkyPreview(darkTheme = true, padding = 20.dp) { AllNodes() }
}

@Preview(name = "Write mode · light")
@Composable
private fun EditorLightPreview() {
    MarkyPreview(padding = 20.dp) { EditorStyles() }
}

@Preview(name = "Write mode · dark")
@Composable
private fun EditorDarkPreview() {
    MarkyPreview(darkTheme = true, padding = 20.dp) { EditorStyles() }
}
