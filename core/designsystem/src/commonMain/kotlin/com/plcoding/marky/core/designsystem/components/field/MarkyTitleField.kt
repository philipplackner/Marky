package com.plcoding.marky.core.designsystem.components.field

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.plcoding.marky.core.designsystem.components.text.MarkyText
import com.plcoding.marky.core.designsystem.preview.MarkyPreview
import com.plcoding.marky.core.designsystem.theme.MarkyTheme

/**
 * The note title, edited in place. `titleLarge`, no border or background.
 *
 * @param contentPadding Defaults to the design's padding on the note screen.
 */
@Composable
fun MarkyTitleField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(start = 20.dp, top = 10.dp, end = 20.dp, bottom = 14.dp),
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        capitalization = KeyboardCapitalization.Sentences,
        imeAction = ImeAction.Next,
    ),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    interactionSource: MutableInteractionSource? = null,
) {
    val colors = MarkyTheme.colors
    val textStyle = MarkyTheme.typography.titleLarge
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        textStyle = textStyle.copy(color = colors.ink),
        cursorBrush = SolidColor(colors.ink),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        interactionSource = interactionSource,
        decorationBox = { innerTextField ->
            Box(modifier = Modifier.padding(contentPadding)) {
                if (value.isEmpty()) {
                    MarkyText(text = placeholder, style = textStyle, color = colors.inkFaint)
                }
                innerTextField()
            }
        },
    )
}

@Composable
private fun TitleFieldStates() {
    Column(
        modifier = Modifier.width(320.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        MarkyTitleField(value = "", onValueChange = {}, placeholder = "Title")
        MarkyTitleField(value = "Trip to Lisbon", onValueChange = {}, placeholder = "Title")
        MarkyTitleField(
            value = "Questions for the next team offsite",
            onValueChange = {},
            placeholder = "Title",
        )
    }
}

@Preview(name = "Title field · light")
@Composable
private fun TitleFieldLightPreview() {
    MarkyPreview(padding = 0.dp) { TitleFieldStates() }
}

@Preview(name = "Title field · dark")
@Composable
private fun TitleFieldDarkPreview() {
    MarkyPreview(darkTheme = true, padding = 0.dp) { TitleFieldStates() }
}
