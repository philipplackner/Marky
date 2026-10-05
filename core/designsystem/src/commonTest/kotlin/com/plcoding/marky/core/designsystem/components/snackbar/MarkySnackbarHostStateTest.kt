package com.plcoding.marky.core.designsystem.components.snackbar

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import assertk.assertions.isTrue
import kotlinx.coroutines.async
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class MarkySnackbarHostStateTest {

    @Test
    fun `snackbar without action stays exactly 4 seconds`() = runTest {
        val state = MarkySnackbarHostState()
        val result = async { state.showSnackbar("Exported \"Trip to Lisbon.md\"") }

        advanceTimeBy(3_999)
        runCurrent()
        assertThat(state.currentSnackbar?.message).isEqualTo("Exported \"Trip to Lisbon.md\"")

        advanceTimeBy(1)
        runCurrent()
        assertThat(state.currentSnackbar).isNull()
        assertThat(result.await()).isEqualTo(MarkySnackbarResult.Dismissed)
    }

    @Test
    fun `snackbar with action stays exactly 6 seconds`() = runTest {
        val state = MarkySnackbarHostState()
        val result = async { state.showSnackbar("Couldn't import \"recipes.pdf\".", actionLabel = "Choose file") }

        advanceTimeBy(5_999)
        runCurrent()
        assertThat(state.currentSnackbar?.actionLabel).isEqualTo("Choose file")

        advanceTimeBy(1)
        runCurrent()
        assertThat(state.currentSnackbar).isNull()
        assertThat(result.await()).isEqualTo(MarkySnackbarResult.Dismissed)
    }

    @Test
    fun `performing the action returns ActionPerformed and hides the snackbar`() = runTest {
        val state = MarkySnackbarHostState()
        val result = async { state.showSnackbar("Couldn't import \"recipes.pdf\".", actionLabel = "Choose file") }
        runCurrent()

        state.currentSnackbar!!.performAction()
        runCurrent()

        assertThat(result.await()).isEqualTo(MarkySnackbarResult.ActionPerformed)
        assertThat(state.currentSnackbar).isNull()
    }

    @Test
    fun `dismissing before the timeout returns Dismissed right away`() = runTest {
        val state = MarkySnackbarHostState()
        val result = async { state.showSnackbar("Exported", actionLabel = "Undo") }
        runCurrent()

        state.currentSnackbar!!.dismiss()
        runCurrent()

        assertThat(result.isCompleted).isTrue()
        assertThat(result.await()).isEqualTo(MarkySnackbarResult.Dismissed)
    }

    @Test
    fun `a second tap after dismissing does not change the result`() = runTest {
        val state = MarkySnackbarHostState()
        val result = async { state.showSnackbar("Exported", actionLabel = "Undo") }
        runCurrent()
        val data = state.currentSnackbar!!

        data.dismiss()
        data.performAction()
        runCurrent()

        assertThat(result.await()).isEqualTo(MarkySnackbarResult.Dismissed)
    }

    @Test
    fun `queued snackbar waits and gets its full duration once shown`() = runTest {
        val state = MarkySnackbarHostState()
        async { state.showSnackbar("First") }
        runCurrent()
        val second = async { state.showSnackbar("Second") }

        advanceTimeBy(2_000)
        runCurrent()
        assertThat(state.currentSnackbar?.message).isEqualTo("First")

        advanceTimeBy(2_000)
        runCurrent()
        assertThat(state.currentSnackbar?.message).isEqualTo("Second")

        advanceTimeBy(3_999)
        runCurrent()
        assertThat(state.currentSnackbar?.message).isEqualTo("Second")
        assertThat(second.isCompleted).isFalse()

        advanceTimeBy(1)
        runCurrent()
        assertThat(state.currentSnackbar).isNull()
    }

    @Test
    fun `cancelling the caller hides the snackbar and frees the queue`() = runTest {
        val state = MarkySnackbarHostState()
        val first = async { state.showSnackbar("First") }
        runCurrent()
        assertThat(state.currentSnackbar).isNotNull()

        first.cancel()
        runCurrent()
        assertThat(state.currentSnackbar).isNull()

        async { state.showSnackbar("Second") }
        runCurrent()
        assertThat(state.currentSnackbar?.message).isEqualTo("Second")
        advanceTimeBy(4_000)
    }
}
