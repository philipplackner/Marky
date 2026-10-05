package com.plcoding.marky.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.plcoding.marky.core.designsystem.generated.resources.Res
import com.plcoding.marky.core.designsystem.generated.resources.bricolage_grotesque
import com.plcoding.marky.core.designsystem.generated.resources.figtree
import com.plcoding.marky.core.designsystem.generated.resources.figtree_italic
import com.plcoding.marky.core.designsystem.generated.resources.jetbrains_mono
import org.jetbrains.compose.resources.Font

/**
 * Type scale of the Marky design system. Sizes, fixed line heights and tracking
 * come from the "Type scale" table in `designs/highlighter/index.html`.
 */
@Immutable
data class MarkyTypography(
    val display: TextStyle,
    val titleLarge: TextStyle,
    val titleMedium: TextStyle,
    val titleSmall: TextStyle,
    val heading1: TextStyle,
    val heading2: TextStyle,
    val heading3: TextStyle,
    val bodyLarge: TextStyle,
    val bodyMedium: TextStyle,
    val labelLarge: TextStyle,
    val labelMedium: TextStyle,
    val labelSmall: TextStyle,
    val code: TextStyle,
)

/**
 * The font families the design system uses. Bricolage Grotesque has an optical-size
 * axis that should match the font size, so it is created per size.
 */
@Immutable
class MarkyFonts internal constructor(
    val figtree: FontFamily,
    val jetBrainsMono: FontFamily,
    private val bricolageBySize: (Float) -> FontFamily,
) {
    fun bricolage(fontSize: TextUnit): FontFamily = bricolageBySize(fontSize.value)
}

internal val CenteredLineHeight = LineHeightStyle(
    alignment = LineHeightStyle.Alignment.Center,
    trim = LineHeightStyle.Trim.None,
)

// Ligatures off so code shows exactly what was typed.
internal const val NoLigatures = "liga 0, calt 0"

@Composable
internal fun rememberMarkyFonts(): MarkyFonts {
    val figtree = FontFamily(
        Font(Res.font.figtree, FontWeight.Normal),
        Font(Res.font.figtree, FontWeight.Medium),
        Font(Res.font.figtree, FontWeight.SemiBold),
        Font(Res.font.figtree, FontWeight.Bold),
        Font(Res.font.figtree, FontWeight.ExtraBold),
        Font(Res.font.figtree_italic, FontWeight.Normal, FontStyle.Italic),
        Font(Res.font.figtree_italic, FontWeight.Bold, FontStyle.Italic),
    )
    val jetBrainsMono = FontFamily(
        Font(Res.font.jetbrains_mono, FontWeight.Normal),
    )
    val bricolageSizes = listOf(16f, 17f, 19f, 20f, 22f, 24f, 28f, 36f, 44f)
    val bricolageFamilies = bricolageSizes.associateWith { size ->
        FontFamily(
            bricolageFont(FontWeight.Bold, size),
            bricolageFont(FontWeight.ExtraBold, size),
        )
    }
    return MarkyFonts(
        figtree = figtree,
        jetBrainsMono = jetBrainsMono,
        bricolageBySize = { size ->
            bricolageFamilies[size] ?: bricolageFamilies.minBy { (key, _) -> kotlin.math.abs(key - size) }.value
        },
    )
}

@Composable
private fun bricolageFont(weight: FontWeight, opticalSize: Float) = Font(
    resource = Res.font.bricolage_grotesque,
    weight = weight,
    variationSettings = FontVariation.Settings(
        FontVariation.weight(weight.weight),
        FontVariation.Setting("opsz", opticalSize),
    ),
)

internal fun markyTypography(fonts: MarkyFonts): MarkyTypography {
    fun bricolage(weight: FontWeight, size: Float, lineHeight: Float, tracking: Float = 0f) = TextStyle(
        fontFamily = fonts.bricolage(size.sp),
        fontWeight = weight,
        fontSize = size.sp,
        lineHeight = lineHeight.sp,
        letterSpacing = tracking.em,
        lineHeightStyle = CenteredLineHeight,
    )

    fun figtree(weight: FontWeight, size: Float, lineHeight: Float) = TextStyle(
        fontFamily = fonts.figtree,
        fontWeight = weight,
        fontSize = size.sp,
        lineHeight = lineHeight.sp,
        lineHeightStyle = CenteredLineHeight,
    )

    return MarkyTypography(
        display = bricolage(FontWeight.ExtraBold, 36f, 40f, -0.03f),
        titleLarge = bricolage(FontWeight.ExtraBold, 28f, 32f, -0.02f),
        titleMedium = bricolage(FontWeight.ExtraBold, 22f, 28f, -0.01f),
        titleSmall = bricolage(FontWeight.Bold, 17f, 22f),
        heading1 = bricolage(FontWeight.ExtraBold, 24f, 30f, -0.02f),
        heading2 = bricolage(FontWeight.ExtraBold, 20f, 26f),
        heading3 = bricolage(FontWeight.Bold, 17f, 22f),
        bodyLarge = figtree(FontWeight.Normal, 16f, 26f),
        bodyMedium = figtree(FontWeight.Normal, 14f, 20f),
        labelLarge = figtree(FontWeight.SemiBold, 15f, 20f),
        labelMedium = figtree(FontWeight.SemiBold, 13f, 16f),
        labelSmall = figtree(FontWeight.Medium, 12f, 16f),
        code = TextStyle(
            fontFamily = fonts.jetBrainsMono,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
            lineHeight = 21.sp,
            fontFeatureSettings = NoLigatures,
            lineHeightStyle = CenteredLineHeight,
        ),
    )
}
