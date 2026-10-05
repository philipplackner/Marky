package com.plcoding.marky.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf

private val LocalMarkyColors = staticCompositionLocalOf { LightMarkyColors }
private val LocalMarkyTypography = staticCompositionLocalOf<MarkyTypography> {
    error("MarkyTypography is only available inside MarkyTheme")
}
private val LocalMarkyFonts = staticCompositionLocalOf<MarkyFonts> {
    error("MarkyFonts are only available inside MarkyTheme")
}

/**
 * The Marky theme. Follows the system light/dark setting by default.
 *
 * Pressed states are expressed with the design's pressed colors, so the Material
 * ripple is turned off for everything inside.
 */
@Composable
fun MarkyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DarkMarkyColors else LightMarkyColors
    val fonts = rememberMarkyFonts()
    val typography = remember(fonts) { markyTypography(fonts) }
    val selectionColors = remember(colors) {
        TextSelectionColors(
            handleColor = colors.ink,
            backgroundColor = colors.selection,
        )
    }
    val colorScheme = remember(colors) { colors.toColorScheme() }

    MaterialTheme(colorScheme = colorScheme) {
        CompositionLocalProvider(
            LocalMarkyColors provides colors,
            LocalMarkyTypography provides typography,
            LocalMarkyFonts provides fonts,
            LocalRippleConfiguration provides null,
            LocalTextSelectionColors provides selectionColors,
            LocalContentColor provides colors.ink,
            LocalTextStyle provides typography.bodyLarge,
            content = content,
        )
    }
}

/**
 * Access to the current Marky tokens.
 */
object MarkyTheme {
    val colors: MarkyColors
        @Composable
        @ReadOnlyComposable
        get() = LocalMarkyColors.current

    val typography: MarkyTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalMarkyTypography.current

    val fonts: MarkyFonts
        @Composable
        @ReadOnlyComposable
        get() = LocalMarkyFonts.current

    val shapes: MarkyShapes = MarkyShapes

    val spacing: MarkySpacing = MarkySpacing
}

// Material 3 bases inside the design system read these, so they must never fall back to Material's purple defaults.
private fun MarkyColors.toColorScheme(): ColorScheme {
    val base = if (isDark) darkColorScheme() else lightColorScheme()
    return base.copy(
        primary = inverse,
        onPrimary = onInverse,
        primaryContainer = highlight,
        onPrimaryContainer = onHighlight,
        inversePrimary = inverseAccent,
        secondary = ink,
        onSecondary = background,
        secondaryContainer = surface,
        onSecondaryContainer = ink,
        tertiary = highlight,
        onTertiary = onHighlight,
        tertiaryContainer = highlightSoft,
        onTertiaryContainer = ink,
        background = background,
        onBackground = ink,
        surface = background,
        onSurface = ink,
        surfaceVariant = surface,
        onSurfaceVariant = inkMuted,
        surfaceTint = background,
        inverseSurface = inverse,
        inverseOnSurface = onInverse,
        error = danger,
        onError = onDanger,
        errorContainer = danger,
        onErrorContainer = onDanger,
        outline = outline,
        outlineVariant = divider,
        scrim = scrim,
        surfaceBright = raised,
        surfaceDim = background,
        surfaceContainerLowest = background,
        surfaceContainerLow = raised,
        surfaceContainer = raised,
        surfaceContainerHigh = raised,
        surfaceContainerHighest = raised,
    )
}
