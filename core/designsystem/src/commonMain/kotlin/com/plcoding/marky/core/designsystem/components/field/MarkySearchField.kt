package com.plcoding.marky.core.designsystem.components.field

import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plcoding.marky.core.designsystem.components.icon.MarkyIcon
import com.plcoding.marky.core.designsystem.components.text.MarkyText
import com.plcoding.marky.core.designsystem.icon.MarkyIcons
import com.plcoding.marky.core.designsystem.preview.MarkyPreview
import com.plcoding.marky.core.designsystem.preview.PressedInteractionSource
import com.plcoding.marky.core.designsystem.theme.MarkyShapes
import com.plcoding.marky.core.designsystem.theme.MarkyTheme

private val FieldHeight = 46.dp
private val FieldBorder = 2.dp

@Composable
private fun fieldTextStyle(): TextStyle = MarkyTheme.typography.bodyLarge.copy(
    fontSize = 15.5.sp,
    lineHeight = 20.sp,
)

/**
 * Search field look-alike on the notes list. It doesn't take input; tapping it
 * opens the search screen.
 */
@Composable
fun MarkySearchEntry(
    placeholder: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource? = null,
) {
    val colors = MarkyTheme.colors
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val isPressed by source.collectIsPressedAsState()
    Surface(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(FieldHeight),
        shape = MarkyShapes.medium,
        color = if (isPressed) colors.surfacePressed else colors.surface,
        contentColor = colors.inkMuted,
        interactionSource = source,
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            MarkyIcon(imageVector = MarkyIcons.Search, contentDescription = null)
            MarkyText(
                text = placeholder,
                style = fieldTextStyle(),
                color = colors.inkMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

/**
 * Search input on the search screen. Gets an ink border while focused; the clear
 * button appears once there is text.
 */
@Composable
fun MarkySearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    onClearClick: () -> Unit,
    clearContentDescription: String,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    interactionSource: MutableInteractionSource? = null,
) {
    SearchField(
        query = query,
        onQueryChange = onQueryChange,
        placeholder = placeholder,
        onClearClick = onClearClick,
        clearContentDescription = clearContentDescription,
        modifier = modifier,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        interactionSource = interactionSource,
        forceFocused = false,
    )
}

@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    onClearClick: () -> Unit,
    clearContentDescription: String,
    modifier: Modifier,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    interactionSource: MutableInteractionSource?,
    forceFocused: Boolean,
) {
    val colors = MarkyTheme.colors
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val isFocused by source.collectIsFocusedAsState()
    val showFocused = isFocused || forceFocused
    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .height(FieldHeight),
        textStyle = fieldTextStyle().copy(color = colors.ink),
        cursorBrush = SolidColor(colors.ink),
        singleLine = true,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        interactionSource = source,
        decorationBox = { innerTextField ->
            Surface(
                shape = MarkyShapes.medium,
                color = if (showFocused) colors.background else colors.surface,
                contentColor = colors.inkMuted,
                modifier = Modifier.border(
                    width = FieldBorder,
                    color = if (showFocused) colors.ink else Color.Transparent,
                    shape = MarkyShapes.medium,
                ),
            ) {
                Row(
                    modifier = Modifier.padding(start = 16.dp, end = if (query.isEmpty()) 14.dp else 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    MarkyIcon(imageVector = MarkyIcons.Search, contentDescription = null)
                    Box(modifier = Modifier.width(10.dp))
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        if (query.isEmpty()) {
                            MarkyText(
                                text = placeholder,
                                style = fieldTextStyle(),
                                color = colors.inkMuted,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        innerTextField()
                    }
                    if (query.isNotEmpty()) {
                        Box(modifier = Modifier.width(4.dp))
                        IconButton(
                            onClick = onClearClick,
                            modifier = Modifier.size(32.dp),
                        ) {
                            MarkyIcon(
                                imageVector = MarkyIcons.Clear,
                                contentDescription = clearContentDescription,
                                tint = colors.inkMuted,
                                size = 20.dp,
                            )
                        }
                    }
                }
            }
        },
    )
}

@Composable
private fun SearchFieldPreviewContent(query: String, focused: Boolean) {
    SearchField(
        query = query,
        onQueryChange = {},
        placeholder = "Search notes",
        onClearClick = {},
        clearContentDescription = "Clear search",
        modifier = Modifier.width(280.dp),
        keyboardOptions = KeyboardOptions.Default,
        keyboardActions = KeyboardActions.Default,
        interactionSource = null,
        forceFocused = focused,
    )
}

@Composable
private fun SearchFieldStates() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        MarkySearchEntry(placeholder = "Search notes", onClick = {}, modifier = Modifier.width(280.dp))
        MarkySearchEntry(
            placeholder = "Search notes",
            onClick = {},
            modifier = Modifier.width(280.dp),
            interactionSource = remember { PressedInteractionSource() },
        )
        SearchFieldPreviewContent(query = "", focused = true)
        SearchFieldPreviewContent(query = "lisbon", focused = true)
        SearchFieldPreviewContent(query = "lisbon", focused = false)
    }
}

@Preview(name = "Search field · light")
@Composable
private fun SearchFieldLightPreview() {
    MarkyPreview { SearchFieldStates() }
}

@Preview(name = "Search field · dark")
@Composable
private fun SearchFieldDarkPreview() {
    MarkyPreview(darkTheme = true) { SearchFieldStates() }
}
