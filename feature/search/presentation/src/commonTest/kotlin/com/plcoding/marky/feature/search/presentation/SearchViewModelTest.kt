package com.plcoding.marky.feature.search.presentation

import androidx.lifecycle.SavedStateHandle
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import com.plcoding.marky.core.domain.util.DataError
import com.plcoding.marky.core.domain.util.Result
import com.plcoding.marky.feature.search.domain.SearchResult
import com.plcoding.marky.feature.search.domain.SearchResults
import com.plcoding.marky.feature.search.domain.Snippet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val dataSource = FakeNoteSearchDataSource()
    private val savedStateHandle = SavedStateHandle()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel() = SearchViewModel(savedStateHandle, dataSource)

    private fun result(id: Long, title: String = "Note $id", snippet: String = "") = SearchResult(
        noteId = id,
        title = title,
        titleMatches = emptyList(),
        snippet = Snippet(snippet, emptyList()),
    )

    private fun results(inTitles: List<Long> = emptyList(), inText: List<Long> = emptyList()) = Result.Success(
        SearchResults(inTitles.map { result(it) }, inText.map { result(it) })
    )

    @Test
    fun `the screen starts in the start state without searching`() = runTest {
        val viewModel = viewModel()
        assertThat(viewModel.state.value.status).isEqualTo(SearchStatus.Start)
        assertThat(dataSource.queries).isEmpty()
    }

    @Test
    fun `typing shows the results for the query`() = runTest {
        val viewModel = viewModel()
        viewModel.onAction(SearchAction.OnQueryChange("lisbon"))
        dataSource.emit("lisbon", results(inTitles = listOf(1), inText = listOf(2, 3)))

        val state = viewModel.state.value
        assertThat(state.status).isEqualTo(SearchStatus.Results)
        assertThat(state.inTitles.map { it.noteId }).containsExactly(1L)
        assertThat(state.inText.map { it.noteId }).containsExactly(2L, 3L)
    }

    @Test
    fun `previous results stay while the next query is still running`() = runTest {
        val viewModel = viewModel()
        viewModel.onAction(SearchAction.OnQueryChange("lis"))
        dataSource.emit("lis", results(inTitles = listOf(1)))
        viewModel.onAction(SearchAction.OnQueryChange("lisx"))

        val state = viewModel.state.value
        assertThat(state.status).isEqualTo(SearchStatus.Results)
        assertThat(state.inTitles.map { it.noteId }).containsExactly(1L)
    }

    @Test
    fun `a late result for an old query is ignored`() = runTest {
        val viewModel = viewModel()
        viewModel.onAction(SearchAction.OnQueryChange("lis"))
        viewModel.onAction(SearchAction.OnQueryChange("lisbon"))
        dataSource.emit("lisbon", results())
        dataSource.emit("lis", results(inTitles = listOf(1)))

        val state = viewModel.state.value
        assertThat(state.status).isEqualTo(SearchStatus.NoResults)
        assertThat(state.inTitles).isEmpty()
    }

    @Test
    fun `clearing resets to the start state and drops results`() = runTest {
        val viewModel = viewModel()
        viewModel.onAction(SearchAction.OnQueryChange("lisbon"))
        dataSource.emit("lisbon", results(inTitles = listOf(1)))
        viewModel.onAction(SearchAction.OnClearClick)

        val state = viewModel.state.value
        assertThat(state.query).isEqualTo("")
        assertThat(state.status).isEqualTo(SearchStatus.Start)
        assertThat(state.inTitles).isEmpty()
    }

    @Test
    fun `whitespace only is the start state and isn't searched`() = runTest {
        val viewModel = viewModel()
        viewModel.onAction(SearchAction.OnQueryChange("   "))
        assertThat(viewModel.state.value.status).isEqualTo(SearchStatus.Start)
        assertThat(dataSource.queries).isEmpty()
    }

    @Test
    fun `a failed search reads as no results`() = runTest {
        val viewModel = viewModel()
        viewModel.onAction(SearchAction.OnQueryChange("lisbon"))
        dataSource.emit("lisbon", Result.Error(DataError.Local.UNKNOWN))
        assertThat(viewModel.state.value.status).isEqualTo(SearchStatus.NoResults)
    }

    @Test
    fun `new results replace old ones when notes change`() = runTest {
        val viewModel = viewModel()
        viewModel.onAction(SearchAction.OnQueryChange("lisbon"))
        dataSource.emit("lisbon", results(inText = listOf(1)))
        dataSource.emit("lisbon", results(inTitles = listOf(1)))

        val state = viewModel.state.value
        assertThat(state.inTitles.map { it.noteId }).containsExactly(1L)
        assertThat(state.inText).isEmpty()
    }

    @Test
    fun `the query survives process death`() = runTest {
        viewModel().onAction(SearchAction.OnQueryChange("lisbon"))
        val restored = viewModel()
        assertThat(restored.state.value.query).isEqualTo("lisbon")
        assertThat(dataSource.queries).containsExactly("lisbon", "lisbon")
    }

    @Test
    fun `blank titles and empty snippets map to missing values`() = runTest {
        val viewModel = viewModel()
        viewModel.onAction(SearchAction.OnQueryChange("lisbon"))
        dataSource.emit(
            "lisbon",
            Result.Success(SearchResults(emptyList(), listOf(result(1, title = "  ", snippet = "")))),
        )
        val result = viewModel.state.value.inText.single()
        assertThat(result.title).isEqualTo(null)
        assertThat(result.snippet).isEqualTo(null)
    }
}
