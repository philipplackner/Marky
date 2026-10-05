package com.plcoding.marky.feature.notes.presentation.note

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.plcoding.marky.core.designsystem.components.button.MarkyButton
import com.plcoding.marky.core.designsystem.components.button.MarkyButtonSize
import com.plcoding.marky.core.designsystem.components.dialog.MarkyDialog
import com.plcoding.marky.core.designsystem.components.empty.MarkyEmptyState
import com.plcoding.marky.core.designsystem.components.field.MarkyTitleField
import com.plcoding.marky.core.designsystem.components.snackbar.MarkySnackbarHost
import com.plcoding.marky.core.designsystem.components.snackbar.MarkySnackbarHostState
import com.plcoding.marky.core.designsystem.icon.MarkyIllustrations
import com.plcoding.marky.core.designsystem.markdown.MarkdownSpacing
import com.plcoding.marky.core.designsystem.theme.MarkyTheme
import com.plcoding.marky.core.markdown.markdownSyntaxSpans
import com.plcoding.marky.core.markdown.parseMarkdown
import com.plcoding.marky.core.presentation.markdown.MarkdownDocumentView
import com.plcoding.marky.core.presentation.util.ObserveAsEvents
import com.plcoding.marky.feature.notes.domain.MarkdownFormat
import com.plcoding.marky.feature.notes.presentation.generated.resources.Res
import com.plcoding.marky.feature.notes.presentation.generated.resources.body_placeholder
import com.plcoding.marky.feature.notes.presentation.generated.resources.cancel
import com.plcoding.marky.feature.notes.presentation.generated.resources.delete
import com.plcoding.marky.feature.notes.presentation.generated.resources.delete_dialog_text
import com.plcoding.marky.feature.notes.presentation.generated.resources.delete_dialog_title
import com.plcoding.marky.feature.notes.presentation.generated.resources.link_text_placeholder
import com.plcoding.marky.feature.notes.presentation.generated.resources.nothing_to_read_text
import com.plcoding.marky.feature.notes.presentation.generated.resources.nothing_to_read_title
import com.plcoding.marky.feature.notes.presentation.generated.resources.start_writing
import com.plcoding.marky.feature.notes.presentation.generated.resources.title_placeholder
import com.plcoding.marky.feature.notes.presentation.generated.resources.untitled
import com.plcoding.marky.feature.notes.presentation.note.components.FormattingToolbar
import com.plcoding.marky.feature.notes.presentation.note.components.MarkdownEditor
import com.plcoding.marky.feature.notes.presentation.note.components.NoteTopBar
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NoteRoot(
    onBack: () -> Unit,
    viewModel: NoteViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { MarkySnackbarHostState() }
    val scope = rememberCoroutineScope()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            NoteEvent.NoteDeleted, NoteEvent.NoteNotFound -> onBack()
            is NoteEvent.ShowSnackbar -> scope.launch {
                snackbarHostState.showSnackbar(event.message.asStringAsync())
            }
        }
    }
    LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
        viewModel.onAction(NoteAction.OnAppBackgrounded)
    }

    NoteScreen(
        state = state,
        onAction = viewModel::onAction,
        onBackClick = onBack,
        snackbarHostState = snackbarHostState,
    )
}

@Composable
fun NoteScreen(
    state: NoteState,
    onAction: (NoteAction) -> Unit,
    onBackClick: () -> Unit,
    snackbarHostState: MarkySnackbarHostState,
) {
    val bodyFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    var isTitleFocused by remember { mutableStateOf(false) }
    var focusBody by remember { mutableStateOf(state.isNewNote) }
    val linkTextPlaceholder = stringResource(Res.string.link_text_placeholder)
    val isKeyboardOpen = isKeyboardVisible()

    LaunchedEffect(focusBody, state.mode, state.isLoading) {
        if (focusBody && state.mode == NoteMode.Write && !state.isLoading) {
            bodyFocusRequester.requestFocus()
            focusBody = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MarkyTheme.colors.background)
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.ime.union(WindowInsets.navigationBars)),
        ) {
            NoteTopBar(
                mode = state.mode,
                isPinned = state.isPinned,
                isMenuOpen = state.isMenuOpen,
                onBackClick = onBackClick,
                onModeSelect = { onAction(NoteAction.OnModeSelect(it)) },
                onMenuClick = { onAction(NoteAction.OnMenuClick) },
                onMenuDismiss = { onAction(NoteAction.OnMenuDismiss) },
                onPinClick = { onAction(NoteAction.OnPinClick) },
                onExportClick = { onAction(NoteAction.OnExportClick) },
                onDeleteClick = { onAction(NoteAction.OnDeleteClick) },
            )
            if (!state.isLoading) {
                val titleField = @Composable {
                    MarkyTitleField(
                        value = state.title,
                        onValueChange = { onAction(NoteAction.OnTitleChange(it)) },
                        placeholder = stringResource(Res.string.title_placeholder),
                        modifier = Modifier.onFocusChanged { isTitleFocused = it.isFocused },
                        keyboardActions = KeyboardActions(
                            onNext = {
                                if (state.mode == NoteMode.Write) {
                                    bodyFocusRequester.requestFocus()
                                } else {
                                    focusManager.clearFocus()
                                }
                            },
                        ),
                    )
                }
                if (state.mode == NoteMode.Read && state.body.text.isBlank()) {
                    titleField()
                    NothingToRead(
                        onStartWritingClick = {
                            focusBody = true
                            onAction(NoteAction.OnStartWritingClick)
                        },
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                    ) {
                        titleField()
                        when (state.mode) {
                            NoteMode.Read -> MarkdownDocumentView(
                                document = state.document,
                                onCheckboxToggle = { onAction(NoteAction.OnCheckboxToggle(it)) },
                                modifier = Modifier.padding(
                                    start = MarkdownSpacing.horizontal,
                                    end = MarkdownSpacing.horizontal,
                                    bottom = ContentBottomSpace,
                                ),
                            )
                            NoteMode.Write -> MarkdownEditor(
                                value = state.body,
                                onValueChange = { onAction(NoteAction.OnBodyChange(it)) },
                                syntaxSpans = state.syntaxSpans,
                                placeholder = stringResource(Res.string.body_placeholder),
                                focusRequester = bodyFocusRequester,
                                modifier = Modifier.padding(bottom = ContentBottomSpace),
                            )
                        }
                    }
                }
                if (state.mode == NoteMode.Write && isKeyboardOpen) {
                    FormattingToolbar(
                        activeFormats = if (isTitleFocused) emptySet() else state.activeFormats,
                        enabled = !isTitleFocused,
                        onFormatClick = { onAction(NoteAction.OnFormatClick(it, linkTextPlaceholder)) },
                    )
                }
            }
        }
        MarkySnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.ime.union(WindowInsets.navigationBars))
                .padding(horizontal = 12.dp, vertical = 16.dp),
        )
    }

    if (state.isDeleteDialogVisible) {
        val title = state.title.trim().ifEmpty { stringResource(Res.string.untitled) }
        MarkyDialog(
            title = stringResource(Res.string.delete_dialog_title, title),
            text = stringResource(Res.string.delete_dialog_text),
            dismissLabel = stringResource(Res.string.cancel),
            confirmLabel = stringResource(Res.string.delete),
            onDismiss = { onAction(NoteAction.OnDeleteDismiss) },
            onConfirm = { onAction(NoteAction.OnDeleteConfirm) },
        )
    }
}

@Composable
private fun ColumnScope.NothingToRead(onStartWritingClick: () -> Unit) {
    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        MarkyEmptyState(
            illustration = MarkyIllustrations.EmptyNote,
            title = stringResource(Res.string.nothing_to_read_title),
            text = stringResource(Res.string.nothing_to_read_text),
        ) {
            MarkyButton(
                text = stringResource(Res.string.start_writing),
                onClick = onStartWritingClick,
                size = MarkyButtonSize.Small,
            )
        }
    }
}

private val ContentBottomSpace = 48.dp

private const val LisbonNote = """Flights are **booked**. Still need the *tram pass*.

## Packing
- [x] Passport
- [x] Adapter plug
- [ ] Sunscreen
- [ ] Rain jacket

> Go to Time Out Market early. It's packed by noon.

```
TP 1353  Fri 08:40
Seat 14C  Gate 07:55
```

[Tram 28 route](https://carris.pt)"""

private const val WelcomeNote = """Marky keeps notes as **Markdown**: plain text with a few symbols for formatting. Tap *Write* to see them.

# Heading 1
## Heading 2
### Heading 3

Text can be **bold**, *italic* or `inline code`.

- A bullet list
- Pin notes you open often

1. A numbered list
2. Export any note as .md

- [x] Open this note
- [ ] Tick this box

> Formatting lives in the text, so your notes work in any editor.

```
code blocks keep
  their spacing
```

[A link opens in the browser](https://www.markdownguide.org)"""

private fun previewState(
    title: String,
    body: String,
    mode: NoteMode,
    selection: TextRange = TextRange(body.length),
    isPinned: Boolean = false,
    isDeleteDialogVisible: Boolean = false,
) = NoteState(
    title = title,
    body = TextFieldValue(body, selection),
    mode = mode,
    isPinned = isPinned,
    isLoading = false,
    document = parseMarkdown(body),
    syntaxSpans = markdownSyntaxSpans(body),
    activeFormats = if (selection.collapsed) emptySet() else setOf(MarkdownFormat.Italic),
    isDeleteDialogVisible = isDeleteDialogVisible,
)

@Composable
private fun NotePreview(state: NoteState, darkTheme: Boolean = false) {
    MarkyTheme(darkTheme = darkTheme) {
        NoteScreen(
            state = state,
            onAction = {},
            onBackClick = {},
            snackbarHostState = remember { MarkySnackbarHostState() },
        )
    }
}

@Preview(name = "Read", widthDp = 360, heightDp = 780)
@Composable
private fun ReadPreview() {
    NotePreview(previewState("Trip to Lisbon", LisbonNote, NoteMode.Read))
}

@Preview(name = "Read, dark", widthDp = 360, heightDp = 780)
@Composable
private fun ReadDarkPreview() {
    NotePreview(previewState("Trip to Lisbon", LisbonNote, NoteMode.Read), darkTheme = true)
}

@Preview(name = "Read, all nodes", widthDp = 360, heightDp = 1300)
@Composable
private fun ReadAllNodesPreview() {
    NotePreview(previewState("Welcome to Marky", WelcomeNote, NoteMode.Read, isPinned = true))
}

@Preview(name = "Read, empty note", widthDp = 360, heightDp = 780)
@Composable
private fun ReadEmptyPreview() {
    NotePreview(previewState("Ideas for Sunday", "", NoteMode.Read))
}

@Preview(name = "Read, empty note, dark", widthDp = 360, heightDp = 780)
@Composable
private fun ReadEmptyDarkPreview() {
    NotePreview(previewState("Ideas for Sunday", "", NoteMode.Read), darkTheme = true)
}

@Preview(name = "Write", widthDp = 360, heightDp = 780)
@Composable
private fun WritePreview() {
    NotePreview(previewState("Trip to Lisbon", LisbonNote, NoteMode.Write))
}

@Preview(name = "Write, dark", widthDp = 360, heightDp = 780)
@Composable
private fun WriteDarkPreview() {
    NotePreview(previewState("Trip to Lisbon", LisbonNote, NoteMode.Write), darkTheme = true)
}

@Preview(name = "Write, text selected", widthDp = 360, heightDp = 780)
@Composable
private fun WriteSelectionPreview() {
    val start = LisbonNote.indexOf("tram pass")
    NotePreview(
        previewState("Trip to Lisbon", LisbonNote, NoteMode.Write, selection = TextRange(start, start + "tram pass".length)),
    )
}

@Preview(name = "New note", widthDp = 360, heightDp = 780)
@Composable
private fun NewNotePreview() {
    NotePreview(NoteState(mode = NoteMode.Write, isLoading = false, isNewNote = true))
}

@Preview(name = "Delete dialog", widthDp = 360, heightDp = 780)
@Composable
private fun DeleteDialogPreview() {
    NotePreview(previewState("Trip to Lisbon", LisbonNote, NoteMode.Read, isDeleteDialogVisible = true))
}

@Preview(name = "Delete dialog, untitled, dark", widthDp = 360, heightDp = 780)
@Composable
private fun DeleteDialogUntitledDarkPreview() {
    NotePreview(previewState("", LisbonNote, NoteMode.Read, isDeleteDialogVisible = true), darkTheme = true)
}

@Preview(name = "Loading", widthDp = 360, heightDp = 780)
@Composable
private fun LoadingPreview() {
    NotePreview(NoteState())
}
