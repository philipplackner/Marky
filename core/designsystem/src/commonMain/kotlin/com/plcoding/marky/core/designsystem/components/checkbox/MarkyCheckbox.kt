package com.plcoding.marky.core.designsystem.components.checkbox

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.plcoding.marky.core.designsystem.preview.MarkyPreview
import com.plcoding.marky.core.designsystem.preview.PressedInteractionSource
import com.plcoding.marky.core.designsystem.theme.MarkyTheme

/**
 * Round checklist checkbox for Read mode: a 22 dp circle inside a 44 dp touch area.
 * Checked fills it yellow with a check mark.
 */
@Composable
fun MarkyCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
) {
    val colors = MarkyTheme.colors
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val isPressed by source.collectIsPressedAsState()
    IconToggleButton(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier
            .size(44.dp)
            .semantics { contentDescription?.let { this.contentDescription = it } },
        enabled = enabled,
        interactionSource = source,
    ) {
        Canvas(modifier = Modifier.size(34.dp)) {
            val radius = 11.dp.toPx()
            if (isPressed && enabled) {
                drawCircle(color = colors.surface, radius = radius + 6.dp.toPx())
            }
            if (checked) {
                drawCircle(color = colors.highlight, radius = radius)
                drawCheckMark(colors.onHighlight)
            } else {
                val ring = 2.dp.toPx()
                drawCircle(
                    color = colors.outline,
                    radius = radius - ring / 2f,
                    style = Stroke(width = ring),
                )
            }
        }
    }
}

// Same geometry as the design's CSS check: a 9 × 5 "L" with a 2.2 border, moved (1, -1) and turned -45°.
private fun DrawScope.drawCheckMark(color: Color) {
    val unit = 1.dp.toPx()
    val path = Path().apply {
        moveTo(-3.4f * unit, -2.5f * unit)
        lineTo(-3.4f * unit, 1.4f * unit)
        lineTo(4.5f * unit, 1.4f * unit)
    }
    rotate(degrees = -45f, pivot = center) {
        translate(left = center.x + unit, top = center.y - unit) {
            drawPath(
                path = path,
                color = color,
                style = Stroke(width = 2.2f * unit, cap = StrokeCap.Butt, join = StrokeJoin.Miter),
            )
        }
    }
}

@Composable
private fun CheckboxStates() {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        MarkyCheckbox(checked = false, onCheckedChange = {})
        MarkyCheckbox(
            checked = false,
            onCheckedChange = {},
            interactionSource = remember { PressedInteractionSource() },
        )
        MarkyCheckbox(checked = true, onCheckedChange = {})
        MarkyCheckbox(
            checked = true,
            onCheckedChange = {},
            interactionSource = remember { PressedInteractionSource() },
        )
    }
}

@Preview(name = "Checkbox · light")
@Composable
private fun CheckboxLightPreview() {
    MarkyPreview { CheckboxStates() }
}

@Preview(name = "Checkbox · dark")
@Composable
private fun CheckboxDarkPreview() {
    MarkyPreview(darkTheme = true) { CheckboxStates() }
}
