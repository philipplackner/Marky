package com.plcoding.marky.feature.search.domain

import com.plcoding.marky.core.domain.util.DataError
import com.plcoding.marky.core.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface NoteSearchDataSource {
    /**
     * Searches all note titles and texts for [query]. Emits again whenever notes change.
     */
    fun search(query: String): Flow<Result<SearchResults, DataError.Local>>
}
