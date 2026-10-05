package com.plcoding.marky.core.files

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import com.plcoding.marky.core.domain.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

@Composable
actual fun rememberFilePicker(
    type: FileType,
    onResult: (Result<PickedFile, FilePickError>) -> Unit
): FilePickerLauncher {
    val context = LocalContext.current.applicationContext
    val scope = rememberCoroutineScope()
    val currentType by rememberUpdatedState(type)
    val currentOnResult by rememberUpdatedState(onResult)

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri == null) {
            return@rememberLauncherForActivityResult
        }
        scope.launch {
            currentOnResult(context.readPickedFile(uri, currentType))
        }
    }

    return remember(launcher) {
        FilePickerLauncher {
            val mimeTypes = currentType.mimeTypes.ifEmpty { listOf("*/*") }
            launcher.launch(mimeTypes.toTypedArray())
        }
    }
}

private suspend fun Context.readPickedFile(
    uri: Uri,
    type: FileType
): Result<PickedFile, FilePickError> = withContext(Dispatchers.IO) {
    val name = displayName(uri) ?: uri.lastPathSegment.orEmpty()
    if (!type.matches(name)) {
        return@withContext Result.Error(FilePickError.WrongFileType(name))
    }
    try {
        val bytes = contentResolver.openInputStream(uri)?.use { it.readBytes() }
            ?: return@withContext Result.Error(FilePickError.Unreadable(name))
        Result.Success(PickedFile(name, bytes))
    } catch (e: IOException) {
        Result.Error(FilePickError.Unreadable(name))
    } catch (e: SecurityException) {
        Result.Error(FilePickError.Unreadable(name))
    }
}

internal fun Context.displayName(uri: Uri): String? {
    return try {
        contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) cursor.getString(0) else null
        }
    } catch (e: SecurityException) {
        null
    } catch (e: IllegalArgumentException) {
        null
    }
}
