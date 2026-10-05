package com.plcoding.marky.core.files

import androidx.compose.runtime.Composable
import com.plcoding.marky.core.domain.util.Result

fun interface FileSaverLauncher {
    /**
     * @param fileName the suggested file name including its extension. It's passed through
     * [sanitizeFileName] first.
     */
    fun launch(fileName: String, mimeType: String, bytes: ByteArray)
}

/**
 * Opens the system save dialog and writes the file to the location the user picks.
 *
 * [onResult] is called once the file is written or writing failed. It isn't called if the user cancels.
 */
@Composable
expect fun rememberFileSaver(
    onResult: (Result<SavedFile, FileSaveError>) -> Unit
): FileSaverLauncher
