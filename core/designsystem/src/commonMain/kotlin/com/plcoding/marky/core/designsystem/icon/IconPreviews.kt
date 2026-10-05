package com.plcoding.marky.core.designsystem.icon

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.plcoding.marky.core.designsystem.components.icon.MarkyIcon
import com.plcoding.marky.core.designsystem.components.text.MarkyText
import com.plcoding.marky.core.designsystem.preview.MarkyPreview
import com.plcoding.marky.core.designsystem.theme.MarkyTheme

private val allIcons: List<ImageVector>
    get() = with(MarkyIcons) {
        listOf(
            Search, Import, Export, Plus, Pin, PinOff, BackChevron, BackArrow,
            MoreVertical, MoreHorizontal, Close, Clear, Trash, BulletList, Checklist,
            Quote, Code, Link, Check, Alert, File,
        )
    }

@Composable
private fun IconSheet() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        FlowRow(
            modifier = Modifier.width(320.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            allIcons.forEach { icon ->
                Column(
                    modifier = Modifier.width(64.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    MarkyIcon(imageVector = icon, contentDescription = icon.name)
                    MarkyText(
                        text = icon.name,
                        style = MarkyTheme.typography.labelSmall,
                        color = MarkyTheme.colors.inkMuted,
                        maxLines = 1,
                    )
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(22.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            MarkyIcon(imageVector = MarkyIcons.Pin, contentDescription = null, size = 24.dp)
            MarkyIcon(imageVector = MarkyIcons.Pin, contentDescription = null, size = 20.dp)
            MarkyIcon(imageVector = MarkyIcons.Pin, contentDescription = null, size = 16.dp, strokeWidth = 2f)
        }
    }
}

@Composable
private fun IllustrationSheet() {
    FlowRow(
        modifier = Modifier.width(280.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Image(imageVector = MarkyIllustrations.NoNotes, contentDescription = null)
        Image(imageVector = MarkyIllustrations.EmptyNote, contentDescription = null)
        Image(imageVector = MarkyIllustrations.SearchStart, contentDescription = null)
        Image(imageVector = MarkyIllustrations.SearchNoResults, contentDescription = null)
    }
}

@Preview(name = "Icons · light")
@Composable
private fun IconsLightPreview() {
    MarkyPreview { IconSheet() }
}

@Preview(name = "Icons · dark")
@Composable
private fun IconsDarkPreview() {
    MarkyPreview(darkTheme = true) { IconSheet() }
}

@Preview(name = "Illustrations · light")
@Composable
private fun IllustrationsLightPreview() {
    MarkyPreview { IllustrationSheet() }
}

@Preview(name = "Illustrations · dark")
@Composable
private fun IllustrationsDarkPreview() {
    MarkyPreview(darkTheme = true) { IllustrationSheet() }
}
