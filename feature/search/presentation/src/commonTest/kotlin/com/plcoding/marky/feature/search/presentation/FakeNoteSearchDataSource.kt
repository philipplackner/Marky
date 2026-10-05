package com.plcoding.marky.feature.search.presentation

import com.plcoding.marky.core.domain.util.DataError
import com.plcoding.marky.core.domain.util.Result
import com.plcoding.marky.feature.search.domain.NoteSearchDataSource
import com.plcoding.marky.feature.search.domain.SearchResults
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

/**
 * Every query gets its own flow, so a test decides when and what each search emits.
 */
class FakeNoteSearchDataSource : NoteSearchDataSource {

    private val flows = mutableMapOf<String, MutableSharedFlow<Result<SearchResults, DataError.Local>>>()
    val queries = mutableListOf<String>()

    fun emit(query: String, result: Result<SearchResults, DataError.Local>) {
        check(flows.getValue(query).tryEmit(result))
    }

    override fun search(query: String): Flow<Result<SearchResults, DataError.Local>> {
        queries += query
        return flows.getOrPut(query) { MutableSharedFlow(replay = 1) }
    }
}
