package com.plcoding.marky.core.files

import androidx.compose.runtime.Composable
import com.plcoding.marky.core.domain.util.Result

fun interface FilePickerLauncher {
    fun launch()
}

/**
 * Opens the system file picker for a single file of [type].
 *
 * [onResult] is called once the picked file is read. It isn't called if the user cancels.
 */
@Composable
expect fun rememberFilePicker(
    type: FileType,
    onResult: (Result<PickedFile, FilePickError>) -> Unit
): FilePickerLauncher
