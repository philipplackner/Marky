package com.plcoding.marky.core.files

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import com.plcoding.marky.core.domain.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.UUID

private class PendingSave(val fileName: String, val bytes: ByteArray)

// Outlives Activity recreation while the dialog is open; a ByteArray can be too large for saved state.
private object PendingSaves {
    private val saves = mutableMapOf<String, PendingSave>()

    fun put(key: String, save: PendingSave) = synchronized(saves) { saves[key] = save }

    fun take(key: String): PendingSave? = synchronized(saves) { saves.remove(key) }
}

private class SaveRequest(val fileName: String, val mimeType: String)

private class CreateDocument : ActivityResultContract<SaveRequest, Uri?>() {
    override fun createIntent(context: Context, input: SaveRequest): Intent {
        return Intent(Intent.ACTION_CREATE_DOCUMENT)
            .addCategory(Intent.CATEGORY_OPENABLE)
            .setType(input.mimeType)
            .putExtra(Intent.EXTRA_TITLE, input.fileName)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return intent.takeIf { resultCode == Activity.RESULT_OK }?.data
    }
}

@Composable
actual fun rememberFileSaver(
    onResult: (Result<SavedFile, FileSaveError>) -> Unit
): FileSaverLauncher {
    val context = LocalContext.current.applicationContext
    val scope = rememberCoroutineScope()
    val currentOnResult by rememberUpdatedState(onResult)
    val key = rememberSaveable { UUID.randomUUID().toString() }

    val launcher = rememberLauncherForActivityResult(CreateDocument()) { uri ->
        val save = PendingSaves.take(key)
        if (uri == null) {
            return@rememberLauncherForActivityResult
        }
        scope.launch {
            currentOnResult(context.writeDocument(uri, save))
        }
    }

    return remember(launcher, key) {
        FileSaverLauncher { fileName, mimeType, bytes ->
            val safeName = sanitizeFileName(fileName)
            PendingSaves.put(key, PendingSave(safeName, bytes))
            launcher.launch(SaveRequest(safeName, mimeType))
        }
    }
}

private suspend fun Context.writeDocument(
    uri: Uri,
    save: PendingSave?
): Result<SavedFile, FileSaveError> = withContext(Dispatchers.IO) {
    val name = displayName(uri) ?: save?.fileName ?: uri.lastPathSegment.orEmpty()
    // The bytes only live in memory, so they're gone if the process died while the dialog was open.
    if (save == null) {
        deleteDocument(uri)
        return@withContext Result.Error(FileSaveError.WriteFailed(name))
    }
    try {
        val stream = contentResolver.openOutputStream(uri, "wt")
            ?: return@withContext Result.Error(FileSaveError.WriteFailed(name))
        stream.use { it.write(save.bytes) }
        Result.Success(SavedFile(name))
    } catch (e: IOException) {
        Result.Error(FileSaveError.WriteFailed(name))
    } catch (e: SecurityException) {
        Result.Error(FileSaveError.WriteFailed(name))
    }
}

private fun Context.deleteDocument(uri: Uri) {
    runCatching { DocumentsContract.deleteDocument(contentResolver, uri) }
}
