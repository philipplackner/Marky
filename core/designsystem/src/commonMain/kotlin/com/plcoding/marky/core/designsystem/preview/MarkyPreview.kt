package com.plcoding.marky.core.designsystem.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.plcoding.marky.core.designsystem.theme.MarkyTheme

@Composable
internal fun MarkyPreview(
    darkTheme: Boolean = false,
    padding: Dp = 16.dp,
    content: @Composable () -> Unit,
) {
    MarkyTheme(darkTheme = darkTheme) {
        Box(
            modifier = Modifier
                .background(MarkyTheme.colors.background)
                .padding(padding),
        ) {
            content()
        }
    }
}
