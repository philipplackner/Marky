package com.plcoding.marky

import androidx.compose.ui.window.ComposeUIViewController
import com.plcoding.marky.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = { initKoin() }
) {
    App()
}
