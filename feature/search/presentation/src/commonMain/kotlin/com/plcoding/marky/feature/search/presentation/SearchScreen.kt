package com.plcoding.marky.feature.search.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.plcoding.marky.core.designsystem.components.bar.MarkyTopBar
import com.plcoding.marky.core.designsystem.components.button.MarkyIconButton
import com.plcoding.marky.core.designsystem.components.empty.MarkyEmptyState
import com.plcoding.marky.core.designsystem.components.field.MarkySearchField
import com.plcoding.marky.core.designsystem.components.list.MarkyDivider
import com.plcoding.marky.core.designsystem.components.list.MarkySearchResult
import com.plcoding.marky.core.designsystem.components.list.MarkySectionLabel
import com.plcoding.marky.core.designsystem.icon.MarkyIcons
import com.plcoding.marky.core.designsystem.icon.MarkyIllustrations
import com.plcoding.marky.core.designsystem.theme.MarkyTheme
import com.plcoding.marky.core.presentation.navigation.searchFieldSharedBounds
import com.plcoding.marky.feature.search.presentation.generated.resources.Res
import com.plcoding.marky.feature.search.presentation.generated.resources.back
import com.plcoding.marky.feature.search.presentation.generated.resources.clear
import com.plcoding.marky.feature.search.presentation.generated.resources.search_no_results_text
import com.plcoding.marky.feature.search.presentation.generated.resources.search_no_results_title
import com.plcoding.marky.feature.search.presentation.generated.resources.search_notes
import com.plcoding.marky.feature.search.presentation.generated.resources.search_start_text
import com.plcoding.marky.feature.search.presentation.generated.resources.search_start_title
import com.plcoding.marky.feature.search.presentation.generated.resources.section_in_text
import com.plcoding.marky.feature.search.presentation.generated.resources.section_in_titles
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SearchRoot(
    onBackClick: () -> Unit,
    onNoteClick: (noteId: Long) -> Unit,
    viewModel: SearchViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    SearchScreen(
        state = state,
        onAction = viewModel::onAction,
        onBackClick = onBackClick,
        onNoteClick = onNoteClick,
    )
}

@Composable
fun SearchScreen(
    state: SearchState,
    onAction: (SearchAction) -> Unit,
    onBackClick: () -> Unit,
    onNoteClick: (noteId: Long) -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    // Saveable, so coming back from a note doesn't pop the keyboard up again.
    var hasFocusedOnOpen by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!hasFocusedOnOpen) {
            focusRequester.requestFocus()
            hasFocusedOnOpen = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MarkyTheme.colors.background)
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal)),
    ) {
        MarkyTopBar {
            MarkyIconButton(
                icon = MarkyIcons.Back,
                contentDescription = stringResource(Res.string.back),
                onClick = onBackClick,
            )
            MarkySearchField(
                query = state.query,
                onQueryChange = { onAction(SearchAction.OnQueryChange(it)) },
                placeholder = stringResource(Res.string.search_notes),
                onClearClick = { onAction(SearchAction.OnClearClick) },
                clearContentDescription = stringResource(Res.string.clear),
                modifier = Modifier
                    .weight(1f)
                    .then(searchFieldSharedBounds())
                    .focusRequester(focusRequester),
                keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() }),
            )
        }
        when (state.status) {
            SearchStatus.Start -> CenteredEmptyState {
                MarkyEmptyState(
                    illustration = MarkyIllustrations.SearchStart,
                    title = stringResource(Res.string.search_start_title),
                    text = stringResource(Res.string.search_start_text),
                )
            }
            SearchStatus.NoResults -> CenteredEmptyState {
                MarkyEmptyState(
                    illustration = MarkyIllustrations.SearchNoResults,
                    title = stringResource(Res.string.search_no_results_title, state.query.trim()),
                    text = stringResource(Res.string.search_no_results_text),
                )
            }
            SearchStatus.Results -> SearchResultsList(
                inTitles = state.inTitles,
                inText = state.inText,
                onNoteClick = onNoteClick,
                onScrollStart = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                },
            )
        }
    }
}

@Composable
private fun CenteredEmptyState(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.ime.union(WindowInsets.navigationBars))
            .padding(bottom = EmptyStateBottomOffset),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

@Composable
private fun SearchResultsList(
    inTitles: List<SearchResultUi>,
    inText: List<SearchResultUi>,
    onNoteClick: (Long) -> Unit,
    onScrollStart: () -> Unit,
) {
    val listState = rememberLazyListState()
    LaunchedEffect(listState.isScrollInProgress) {
        if (listState.isScrollInProgress) {
            onScrollStart()
        }
    }
    val bottomInset = WindowInsets.ime.union(WindowInsets.navigationBars).asPaddingValues().calculateBottomPadding()
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = bottomInset + ListBottomSpace),
    ) {
        resultGroup(
            key = "titles",
            label = { stringResource(Res.string.section_in_titles) },
            results = inTitles,
            topPadding = FirstGroupTopPadding,
            onNoteClick = onNoteClick,
        )
        resultGroup(
            key = "text",
            label = { stringResource(Res.string.section_in_text) },
            results = inText,
            topPadding = if (inTitles.isEmpty()) FirstGroupTopPadding else NextGroupTopPadding,
            onNoteClick = onNoteClick,
        )
    }
}

private fun LazyListScope.resultGroup(
    key: String,
    label: @Composable () -> String,
    results: List<SearchResultUi>,
    topPadding: Dp,
    onNoteClick: (Long) -> Unit,
) {
    if (results.isEmpty()) {
        return
    }
    item(key = "label_$key") {
        MarkySectionLabel(
            text = label(),
            count = results.size,
            modifier = Modifier.padding(top = topPadding),
        )
    }
    itemsIndexed(items = results, key = { _, result -> "${key}_${result.noteId}" }) { index, result ->
        Column(modifier = Modifier.fillMaxWidth()) {
            if (index > 0) {
                MarkyDivider()
            }
            MarkySearchResult(
                title = result.title,
                snippet = result.snippet,
                onClick = { onNoteClick(result.noteId) },
                titleMatches = result.titleMatches,
                snippetMatches = result.snippetMatches,
            )
        }
    }
}

private val FirstGroupTopPadding = 8.dp
private val NextGroupTopPadding = 18.dp
private val EmptyStateBottomOffset = 40.dp
private val ListBottomSpace = 24.dp

private fun previewResult(
    id: Long,
    title: String?,
    snippet: String?,
    query: String,
) = SearchResultUi(
    noteId = id,
    title = title,
    titleMatches = title?.let { matchesOf(it, query) }.orEmpty(),
    snippet = snippet,
    snippetMatches = snippet?.let { matchesOf(it, query) }.orEmpty(),
)

private fun matchesOf(text: String, query: String): List<IntRange> =
    Regex("\\b${Regex.escape(query)}", RegexOption.IGNORE_CASE)
        .findAll(text)
        .map { it.range.first until it.range.first + query.length }
        .toList()

private val PreviewResults = SearchState(
    query = "lisbon",
    searchedQuery = "lisbon",
    inTitles = listOf(
        previewResult(1, "Trip to Lisbon", "Flights are booked. Still need the tram pass and a rain jacket.", "lisbon"),
    ),
    inText = listOf(
        previewResult(2, "Q4 roadmap call", "…empty states. The offsite in Lisbon is still on for November.", "lisbon"),
        previewResult(3, "Reading list", "…a re-read of Dune, and the Pessoa book I found in Lisbon.", "lisbon"),
        previewResult(4, "Gift ideas", "The blue tiles from Lisbon for Ana, a cookbook for Dad, running socks for Sam.", "lisbon"),
    ),
)

private val PreviewPartialWord = SearchState(
    query = "pack",
    searchedQuery = "pack",
    inTitles = listOf(
        previewResult(9, "Packing for camping", "Tent, two sleeping mats, the small stove.", "pack"),
    ),
    inText = listOf(
        previewResult(1, "Trip to Lisbon", "…and a rain jacket. Packing: passport, sunscreen. It's packed by noon.", "pack"),
    ),
)

private val PreviewOnlyText = SearchState(
    query = "boiler",
    searchedQuery = "boiler",
    inText = listOf(
        previewResult(6, null, "Call the landlord about the boiler before Friday.", "boiler"),
        previewResult(
            id = 12,
            title = "House",
            snippet = "…the inspection report says the boiler is twelve years old and should be replaced before winter, " +
                "the radiators in the back rooms need bleeding and the thermostat runs two degrees cold.",
            query = "boiler",
        ),
    ),
)

private val PreviewOnlyTitles = SearchState(
    query = "ideas",
    searchedQuery = "ideas",
    inTitles = listOf(
        previewResult(13, "Ideas", "Half-baked app ideas, collected on the train.", "ideas"),
        previewResult(14, "Ideas", null, "ideas"),
    ),
)

@Composable
private fun SearchPreview(state: SearchState, darkTheme: Boolean = false) {
    MarkyTheme(darkTheme = darkTheme) {
        SearchScreen(state = state, onAction = {}, onBackClick = {}, onNoteClick = {})
    }
}

@Preview(name = "Start typing", widthDp = 360, heightDp = 780)
@Composable
private fun StartPreview() {
    SearchPreview(SearchState())
}

@Preview(name = "Start typing, dark", widthDp = 360, heightDp = 780)
@Composable
private fun StartDarkPreview() {
    SearchPreview(SearchState(), darkTheme = true)
}

@Preview(name = "Results", widthDp = 360, heightDp = 780)
@Composable
private fun ResultsPreview() {
    SearchPreview(PreviewResults)
}

@Preview(name = "Results, dark", widthDp = 360, heightDp = 780)
@Composable
private fun ResultsDarkPreview() {
    SearchPreview(PreviewResults, darkTheme = true)
}

@Preview(name = "Partial word", widthDp = 360, heightDp = 780)
@Composable
private fun PartialWordPreview() {
    SearchPreview(PreviewPartialWord)
}

@Preview(name = "Only text matches, long snippet, untitled", widthDp = 360, heightDp = 780)
@Composable
private fun OnlyTextPreview() {
    SearchPreview(PreviewOnlyText)
}

@Preview(name = "Only title matches, no text", widthDp = 360, heightDp = 780)
@Composable
private fun OnlyTitlesPreview() {
    SearchPreview(PreviewOnlyTitles)
}

@Preview(name = "No results", widthDp = 360, heightDp = 780)
@Composable
private fun NoResultsPreview() {
    SearchPreview(SearchState(query = "kyoto", searchedQuery = "kyoto"))
}

@Preview(name = "No results, dark", widthDp = 360, heightDp = 780)
@Composable
private fun NoResultsDarkPreview() {
    SearchPreview(SearchState(query = "kyoto", searchedQuery = "kyoto"), darkTheme = true)
}
