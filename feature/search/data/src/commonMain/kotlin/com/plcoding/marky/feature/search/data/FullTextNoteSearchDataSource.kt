package com.plcoding.marky.feature.search.data

import com.plcoding.marky.core.domain.note.Note
import com.plcoding.marky.core.domain.note.NoteLocalDataSource
import com.plcoding.marky.core.domain.util.DataError
import com.plcoding.marky.core.domain.util.Result
import com.plcoding.marky.core.domain.util.map
import com.plcoding.marky.core.markdown.toPlainText
import com.plcoding.marky.feature.search.domain.NoteSearchDataSource
import com.plcoding.marky.feature.search.domain.SearchCandidate
import com.plcoding.marky.feature.search.domain.SearchResults
import com.plcoding.marky.feature.search.domain.rankResults
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest

/**
 * Narrows notes down with the full-text index, then matches, ranks and cuts snippets from the
 * plain text, so only what's visible in the note counts as a match.
 */
class FullTextNoteSearchDataSource(
    private val noteLocalDataSource: NoteLocalDataSource,
    private val dispatcher: CoroutineDispatcher,
) : NoteSearchDataSource {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun search(query: String): Flow<Result<SearchResults, DataError.Local>> =
        noteLocalDataSource.observeNotes()
            .mapLatest {
                noteLocalDataSource.searchNotes(query).map { notes ->
                    rankResults(notes.map { it.toSearchCandidate() }, query)
                }
            }
            .flowOn(dispatcher)
}

private fun Note.toSearchCandidate() = SearchCandidate(
    noteId = id,
    title = title,
    plainText = content.toPlainText(),
    lastEditedAt = lastEditedAt,
)
