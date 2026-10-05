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
        Result.Error(if (isDiskFullMessage(e.message)) DataError.Local.DISK_FULL else DataError.Local.UNKNOWN)
    } catch (e: Exception) {
        Result.Error(DataError.Local.UNKNOWN)
    }
}

/**
 * The bundled driver has no typed exception for SQLITE_FULL (code 13), so the error message
 * is the only signal.
 */
@PublishedApi
internal fun isDiskFullMessage(message: String?): Boolean {
    if (message == null) {
        return false
    }
    return message.contains("Error code: 13,") || message.contains("disk is full", ignoreCase = true)
}
