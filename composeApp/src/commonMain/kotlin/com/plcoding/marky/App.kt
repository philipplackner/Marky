package com.plcoding.marky

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.plcoding.marky.core.designsystem.theme.MarkyTheme

@Composable
fun App() {
    MarkyTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MarkyTheme.colors.background),
        )
    }
}
