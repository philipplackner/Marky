package com.plcoding.marky.core.presentation.navigation

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.SharedTransitionScope.ResizeMode.Companion.RemeasureToBounds
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import com.plcoding.marky.core.designsystem.theme.MarkyShapes

/**
 * The [SharedTransitionScope] around the app's nav host. Null outside of it, e.g. in previews.
 */
val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }

/**
 * The enter/exit scope of the current navigation destination. Null outside of a nav host.
 */
val LocalNavAnimatedVisibilityScope = compositionLocalOf<AnimatedVisibilityScope?> { null }

/**
 * Lets the search field on the notes list morph into the field on the search screen and back.
 * Lives in core because both features need the same key.
 *
 * @return an empty [Modifier] when there's no shared transition, so previews still work.
 */
@Composable
fun searchFieldSharedBounds(): Modifier {
    val sharedTransitionScope = LocalSharedTransitionScope.current ?: return Modifier
    val animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current ?: return Modifier
    return with(sharedTransitionScope) {
        Modifier.sharedBounds(
            sharedContentState = rememberSharedContentState(SEARCH_FIELD_KEY),
            animatedVisibilityScope = animatedVisibilityScope,
            resizeMode = RemeasureToBounds,
            clipInOverlayDuringTransition = OverlayClip(MarkyShapes.medium),
        )
    }
}

private const val SEARCH_FIELD_KEY = "search_field"
