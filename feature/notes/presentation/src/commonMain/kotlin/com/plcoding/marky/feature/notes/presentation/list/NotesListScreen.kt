package com.plcoding.marky.feature.notes.presentation.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.plcoding.marky.core.designsystem.components.bar.MarkyCenterTopBar
import com.plcoding.marky.core.designsystem.components.bar.MarkyLargeTitle
import com.plcoding.marky.core.designsystem.components.bar.MarkyTopBar
import com.plcoding.marky.core.designsystem.components.button.MarkyButton
import com.plcoding.marky.core.designsystem.components.button.MarkyButtonStyle
import com.plcoding.marky.core.designsystem.components.button.MarkyFab
import com.plcoding.marky.core.designsystem.components.button.MarkyIconButton
import com.plcoding.marky.core.designsystem.components.button.MarkyIconButtonStyle
import com.plcoding.marky.core.designsystem.components.empty.MarkyEmptyState
import com.plcoding.marky.core.designsystem.components.field.MarkySearchEntry
import com.plcoding.marky.core.designsystem.components.list.MarkyDivider
import com.plcoding.marky.core.designsystem.components.list.MarkyNoteRow
import com.plcoding.marky.core.designsystem.components.list.MarkyPinnedTile
import com.plcoding.marky.core.designsystem.components.list.MarkySectionLabel
import com.plcoding.marky.core.designsystem.icon.MarkyIcons
import com.plcoding.marky.core.designsystem.icon.MarkyIllustrations
import com.plcoding.marky.core.designsystem.theme.MarkySpacing
import com.plcoding.marky.core.designsystem.theme.MarkyTheme
import com.plcoding.marky.feature.notes.domain.TimeSinceEdit
import com.plcoding.marky.feature.notes.presentation.generated.resources.Res
import com.plcoding.marky.feature.notes.presentation.generated.resources.import_md_file
import com.plcoding.marky.feature.notes.presentation.generated.resources.new_note
import com.plcoding.marky.feature.notes.presentation.generated.resources.no_notes_text
import com.plcoding.marky.feature.notes.presentation.generated.resources.no_notes_title
import com.plcoding.marky.feature.notes.presentation.generated.resources.notes_title
import com.plcoding.marky.feature.notes.presentation.generated.resources.search
import com.plcoding.marky.feature.notes.presentation.generated.resources.search_notes
import com.plcoding.marky.feature.notes.presentation.generated.resources.section_pinned
import com.plcoding.marky.feature.notes.presentation.generated.resources.section_recent
import com.plcoding.marky.feature.notes.presentation.util.asString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NotesListRoot(
    onNoteClick: (noteId: Long) -> Unit,
    onNewNoteClick: () -> Unit,
    onSearchClick: () -> Unit,
    onImportClick: () -> Unit,
    viewModel: NotesListViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    NotesListScreen(
        state = state,
        onNoteClick = onNoteClick,
        onNewNoteClick = onNewNoteClick,
        onSearchClick = onSearchClick,
        onImportClick = onImportClick,
    )
}

@Composable
fun NotesListScreen(
    state: NotesListState,
    onNoteClick: (noteId: Long) -> Unit,
    onNewNoteClick: () -> Unit,
    onSearchClick: () -> Unit,
    onImportClick: () -> Unit,
    listState: LazyListState = rememberLazyListState(),
) {
    val isCollapsed by remember(listState) { derivedStateOf { listState.firstVisibleItemIndex > 0 } }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MarkyTheme.colors.background)
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal)),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (isCollapsed && !state.isEmpty) {
                CollapsedBar(onSearchClick = onSearchClick, onImportClick = onImportClick)
            } else {
                ExpandedBar(onImportClick = onImportClick)
            }
            if (state.isEmpty) {
                ListHeader(onSearchClick = onSearchClick)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.navigationBars),
                    contentAlignment = Alignment.Center,
                ) {
                    NoNotesState(onNewNoteClick = onNewNoteClick, onImportClick = onImportClick)
                }
            } else if (!state.isLoading) {
                NotesList(
                    state = state,
                    listState = listState,
                    onNoteClick = onNoteClick,
                    onSearchClick = onSearchClick,
                )
            }
        }
        if (!state.isEmpty && !state.isLoading) {
            MarkyFab(
                text = stringResource(Res.string.new_note),
                icon = MarkyIcons.Plus,
                onClick = onNewNoteClick,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(bottom = FabBottomOffset),
            )
        }
    }
}

@Composable
private fun ExpandedBar(onImportClick: () -> Unit) {
    MarkyTopBar {
        Spacer(modifier = Modifier.weight(1f))
        MarkyIconButton(
            icon = MarkyIcons.Import,
            contentDescription = stringResource(Res.string.import_md_file),
            onClick = onImportClick,
        )
    }
}

@Composable
private fun CollapsedBar(onSearchClick: () -> Unit, onImportClick: () -> Unit) {
    MarkyCenterTopBar(title = stringResource(Res.string.notes_title)) {
        MarkyIconButton(
            icon = MarkyIcons.Search,
            contentDescription = stringResource(Res.string.search),
            onClick = onSearchClick,
            style = MarkyIconButtonStyle.Plain,
        )
        MarkyIconButton(
            icon = MarkyIcons.Import,
            contentDescription = stringResource(Res.string.import_md_file),
            onClick = onImportClick,
            style = MarkyIconButtonStyle.Plain,
        )
    }
}

@Composable
private fun ListHeader(onSearchClick: () -> Unit) {
    Column {
        MarkyLargeTitle(text = stringResource(Res.string.notes_title))
        MarkySearchEntry(
            placeholder = stringResource(Res.string.search_notes),
            onClick = onSearchClick,
            modifier = Modifier.padding(
                start = MarkySpacing.screenHorizontal,
                end = MarkySpacing.screenHorizontal,
                bottom = 18.dp,
            ),
        )
    }
}

@Composable
private fun NotesList(
    state: NotesListState,
    listState: LazyListState,
    onNoteClick: (Long) -> Unit,
    onSearchClick: () -> Unit,
) {
    val navigationBarBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = navigationBarBottom + ListBottomSpace),
    ) {
        item(key = "header") {
            ListHeader(onSearchClick = onSearchClick)
        }
        if (state.pinnedNotes.isNotEmpty()) {
            item(key = "pinnedLabel") {
                MarkySectionLabel(text = stringResource(Res.string.section_pinned))
            }
            item(key = "pinned") {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = MarkySpacing.screenHorizontal),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(bottom = MarkySpacing.section),
                ) {
                    items(items = state.pinnedNotes, key = { it.id }) { note ->
                        MarkyPinnedTile(
                            title = note.title,
                            preview = note.preview.orEmpty(),
                            timestamp = note.timeSinceEdit.asString(),
                            onClick = { onNoteClick(note.id) },
                        )
                    }
                }
            }
            if (state.recentNotes.isNotEmpty()) {
                item(key = "recentLabel") {
                    MarkySectionLabel(text = stringResource(Res.string.section_recent))
                }
            }
        }
        itemsIndexed(items = state.recentNotes, key = { _, note -> note.id }) { index, note ->
            Column {
                MarkyNoteRow(
                    title = note.title,
                    timestamp = note.timeSinceEdit.asString(),
                    preview = note.preview,
                    onClick = { onNoteClick(note.id) },
                )
                if (index < state.recentNotes.lastIndex) {
                    MarkyDivider()
                }
            }
        }
    }
}

@Composable
private fun NoNotesState(onNewNoteClick: () -> Unit, onImportClick: () -> Unit) {
    MarkyEmptyState(
        illustration = MarkyIllustrations.NoNotes,
        title = stringResource(Res.string.no_notes_title),
        text = stringResource(Res.string.no_notes_text),
    ) {
        MarkyButton(
            text = stringResource(Res.string.new_note),
            onClick = onNewNoteClick,
            icon = MarkyIcons.Plus,
        )
        MarkyButton(
            text = stringResource(Res.string.import_md_file),
            onClick = onImportClick,
            style = MarkyButtonStyle.Text,
            icon = MarkyIcons.Import,
        )
    }
}

private val FabBottomOffset = 34.dp
private val ListBottomSpace = 100.dp

private val PreviewPinned = listOf(
    NoteUi(101, "Weekly groceries", "Oat milk, lemons, sourdough, chickpeas", TimeSinceEdit.Hours(1)),
    NoteUi(102, "Reading list", "The Overstory, Klara and the Sun, Dune", TimeSinceEdit.Days(3)),
)

private val PreviewManyPinned = PreviewPinned + listOf(
    NoteUi(103, null, "Door code 4471, bins go out on Tuesday", TimeSinceEdit.Weeks(1)),
    NoteUi(104, "Index", "Where everything lives", TimeSinceEdit.Weeks(3)),
)

private val PreviewRecent = listOf(
    NoteUi(1, "Trip to Lisbon", "Flights are booked. Still need the tram pass and a rain jacket.", TimeSinceEdit.Minutes(4)),
    NoteUi(2, "Q4 roadmap call", "Ship search before import. Ana owns the empty states.", TimeSinceEdit.Hours(2)),
    NoteUi(3, "Welcome to Marky", "Write in Markdown, read it formatted.", TimeSinceEdit.Days(2)),
    NoteUi(4, "Gift ideas", "The blue tiles from Lisbon for Ana, a cookbook for Dad.", TimeSinceEdit.Weeks(1)),
    NoteUi(5, "Running plan", "Three easy runs, one long run on Sunday.", TimeSinceEdit.Date(12, 8, null)),
    NoteUi(6, null, "Call the landlord about the boiler before Friday.", TimeSinceEdit.Date(9, 8, null)),
    NoteUi(7, "Book club questions", "Why does the narrator never say her name?", TimeSinceEdit.Date(2, 8, null)),
    NoteUi(8, "Apartment viewing", "Bright, quiet street, but the kitchen is tiny.", TimeSinceEdit.Date(28, 7, null)),
    NoteUi(9, "Packing for camping", "Tent, two sleeping mats, the small stove.", TimeSinceEdit.Date(14, 7, null)),
    NoteUi(10, "Empty note", null, TimeSinceEdit.Date(3, 7, null)),
    NoteUi(11, "Podcast notes", "Episode 42 on habit stacking was the good one.", TimeSinceEdit.Date(21, 6, 2025)),
)

private val PreviewFirstLaunch = NotesListState(
    pinnedNotes = listOf(
        NoteUi(1, "Welcome to Marky", "Start here. Write in Markdown, read it formatted.", TimeSinceEdit.JustNow),
    ),
    recentNotes = listOf(
        NoteUi(2, "Markdown basics", "Headings, bold, italic, lists and links in one page.", TimeSinceEdit.JustNow),
        NoteUi(3, "A sample checklist", "Switch to Read and tick the boxes.", TimeSinceEdit.JustNow),
        NoteUi(4, "Import and export", "Bring in any .md file, or take a note to another app.", TimeSinceEdit.JustNow),
    ),
    isLoading = false,
)

@Composable
private fun NotesListPreview(
    state: NotesListState,
    darkTheme: Boolean = false,
    firstVisibleItemIndex: Int = 0,
) {
    MarkyTheme(darkTheme = darkTheme) {
        NotesListScreen(
            state = state,
            onNoteClick = {},
            onNewNoteClick = {},
            onSearchClick = {},
            onImportClick = {},
            listState = rememberLazyListState(initialFirstVisibleItemIndex = firstVisibleItemIndex),
        )
    }
}

@Preview(name = "Default", widthDp = 360, heightDp = 780)
@Composable
private fun DefaultPreview() {
    NotesListPreview(NotesListState(PreviewPinned, PreviewRecent, isLoading = false))
}

@Preview(name = "Default, dark", widthDp = 360, heightDp = 780)
@Composable
private fun DefaultDarkPreview() {
    NotesListPreview(NotesListState(PreviewPinned, PreviewRecent, isLoading = false), darkTheme = true)
}

@Preview(name = "Scrolled", widthDp = 360, heightDp = 780)
@Composable
private fun ScrolledPreview() {
    NotesListPreview(NotesListState(PreviewPinned, PreviewRecent, isLoading = false), firstVisibleItemIndex = 5)
}

@Preview(name = "Scrolled, dark", widthDp = 360, heightDp = 780)
@Composable
private fun ScrolledDarkPreview() {
    NotesListPreview(
        NotesListState(PreviewPinned, PreviewRecent, isLoading = false),
        darkTheme = true,
        firstVisibleItemIndex = 5,
    )
}

@Preview(name = "Many pinned notes", widthDp = 360, heightDp = 780)
@Composable
private fun ManyPinnedPreview() {
    NotesListPreview(NotesListState(PreviewManyPinned, PreviewRecent, isLoading = false))
}

@Preview(name = "No pinned notes", widthDp = 360, heightDp = 780)
@Composable
private fun NoPinnedPreview() {
    NotesListPreview(NotesListState(emptyList(), PreviewRecent, isLoading = false))
}

@Preview(name = "Only pinned notes", widthDp = 360, heightDp = 780)
@Composable
private fun OnlyPinnedPreview() {
    NotesListPreview(NotesListState(PreviewPinned, emptyList(), isLoading = false))
}

@Preview(name = "First launch", widthDp = 360, heightDp = 780)
@Composable
private fun FirstLaunchPreview() {
    NotesListPreview(PreviewFirstLaunch)
}

@Preview(name = "No notes", widthDp = 360, heightDp = 780)
@Composable
private fun EmptyPreview() {
    NotesListPreview(NotesListState(isLoading = false))
}

@Preview(name = "No notes, dark", widthDp = 360, heightDp = 780)
@Composable
private fun EmptyDarkPreview() {
    NotesListPreview(NotesListState(isLoading = false), darkTheme = true)
}

@Preview(name = "Loading", widthDp = 360, heightDp = 780)
@Composable
private fun LoadingPreview() {
    NotesListPreview(NotesListState())
}
