@file:OptIn(ExperimentalForeignApi::class)

package com.plcoding.marky.core.files

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.uikit.LocalUIViewController
import com.plcoding.marky.core.domain.util.Result
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSFileManager
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.NSUUID
import platform.Foundation.writeToURL
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerViewController
import platform.darwin.NSObject

@Composable
actual fun rememberFileSaver(
    onResult: (Result<SavedFile, FileSaveError>) -> Unit
): FileSaverLauncher {
    val viewController = LocalUIViewController.current
    val currentOnResult by rememberUpdatedState(onResult)

    // UIDocumentPickerViewController holds its delegate weakly.
    val delegate = remember { SaverDelegate { currentOnResult(it) } }

    return remember(viewController, delegate) {
        FileSaverLauncher { fileName, _, bytes ->
            val safeName = sanitizeFileName(fileName)
            val directory = writeTemporaryFile(safeName, bytes)
            if (directory == null) {
                currentOnResult(Result.Error(FileSaveError.WriteFailed(safeName)))
                return@FileSaverLauncher
            }
            delegate.temporaryDirectory = directory
            val file = directory.URLByAppendingPathComponent(safeName) ?: return@FileSaverLauncher
            val picker = UIDocumentPickerViewController(forExportingURLs = listOf(file), asCopy = true)
            picker.delegate = delegate
            viewController.presentViewController(picker, animated = true, completion = null)
        }
    }
}

private class SaverDelegate(
    private val onSaved: (Result<SavedFile, FileSaveError>) -> Unit
) : NSObject(), UIDocumentPickerDelegateProtocol {

    var temporaryDirectory: NSURL? = null

    override fun documentPicker(
        controller: UIDocumentPickerViewController,
        didPickDocumentsAtURLs: List<*>
    ) {
        removeTemporaryDirectory()
        val name = (didPickDocumentsAtURLs.firstOrNull() as? NSURL)?.lastPathComponent.orEmpty()
        onSaved(Result.Success(SavedFile(name)))
    }

    override fun documentPickerWasCancelled(controller: UIDocumentPickerViewController) {
        removeTemporaryDirectory()
    }

    private fun removeTemporaryDirectory() {
        temporaryDirectory?.let { NSFileManager.defaultManager.removeItemAtURL(it, error = null) }
        temporaryDirectory = null
    }
}

private fun writeTemporaryFile(fileName: String, bytes: ByteArray): NSURL? {
    val directory = NSURL.fileURLWithPath(NSTemporaryDirectory()).URLByAppendingPathComponent(NSUUID().UUIDString)
        ?: return null
    val fileManager = NSFileManager.defaultManager
    val created = fileManager.createDirectoryAtURL(
        directory,
        withIntermediateDirectories = true,
        attributes = null,
        error = null
    )
    val file = directory.URLByAppendingPathComponent(fileName)
    if (!created || file == null || !bytes.toNSData().writeToURL(file, atomically = true)) {
        fileManager.removeItemAtURL(directory, error = null)
        return null
    }
    return directory
}
