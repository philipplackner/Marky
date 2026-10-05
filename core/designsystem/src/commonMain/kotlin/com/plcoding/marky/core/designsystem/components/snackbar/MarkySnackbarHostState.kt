package com.plcoding.marky.core.designsystem.components.snackbar

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

enum class MarkySnackbarResult {
    Dismissed,
    ActionPerformed,
}

/**
 * A snackbar that is currently shown by a [MarkySnackbarHost].
 */
@Stable
class MarkySnackbarData internal constructor(
    val message: String,
    val actionLabel: String?,
    private val result: CompletableDeferred<MarkySnackbarResult>,
) {
    fun performAction() {
        result.complete(MarkySnackbarResult.ActionPerformed)
    }

    fun dismiss() {
        result.complete(MarkySnackbarResult.Dismissed)
    }
}

/**
 * Shows one snackbar at a time; later calls wait for the current one. A snackbar
 * stays 6 seconds with an action and 4 seconds without.
 */
@Stable
class MarkySnackbarHostState {
    private val mutex = Mutex()

    var currentSnackbar: MarkySnackbarData? by mutableStateOf(null)
        private set

    suspend fun showSnackbar(
        message: String,
        actionLabel: String? = null,
    ): MarkySnackbarResult = mutex.withLock {
        val result = CompletableDeferred<MarkySnackbarResult>()
        try {
            currentSnackbar = MarkySnackbarData(message, actionLabel, result)
            withTimeoutOrNull(durationFor(actionLabel)) { result.await() }
                ?: MarkySnackbarResult.Dismissed
        } finally {
            currentSnackbar = null
        }
    }

    private fun durationFor(actionLabel: String?): Duration =
        if (actionLabel != null) WithActionDuration else WithoutActionDuration

    internal companion object {
        val WithActionDuration = 6.seconds
        val WithoutActionDuration = 4.seconds
    }
}
