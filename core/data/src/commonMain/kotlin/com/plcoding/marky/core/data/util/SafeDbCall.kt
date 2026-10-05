package com.plcoding.marky.core.data.util

import androidx.sqlite.SQLiteException
import com.plcoding.marky.core.domain.util.DataError
import com.plcoding.marky.core.domain.util.Result
import kotlin.coroutines.cancellation.CancellationException

suspend inline fun <T> safeDbCall(block: suspend () -> Result<T, DataError.Local>): Result<T, DataError.Local> {
    return try {
        block()
    } catch (e: CancellationException) {
        throw e
    } catch (e: SQLiteException) {
        Result.Error(sqliteErrorToDataError(e.message))
    } catch (e: Exception) {
        Result.Error(DataError.Local.UNKNOWN)
    }
}

private const val SQLITE_FULL = 13
private const val PRIMARY_RESULT_CODE_MASK = 0xFF
private val RESULT_CODE_REGEX = Regex("""^Error code: (\d+)""")

/**
 * Maps the message of an [SQLiteException] to a [DataError.Local].
 *
 * [SQLiteException] has no result code property. The drivers put the code in the message
 * instead, always formatted as `Error code: <code>, message: <sqlite message>`
 * (see `androidx.sqlite.throwSQLiteException`). On iOS the code is an extended result code,
 * so only its lowest byte, the primary code, is compared.
 */
@PublishedApi
internal fun sqliteErrorToDataError(message: String?): DataError.Local {
    val resultCode = message
        ?.let { RESULT_CODE_REGEX.find(it) }
        ?.groupValues?.get(1)
        ?.toIntOrNull()
        ?: return DataError.Local.UNKNOWN
    return when (resultCode and PRIMARY_RESULT_CODE_MASK) {
        SQLITE_FULL -> DataError.Local.DISK_FULL
        else -> DataError.Local.UNKNOWN
    }
}
