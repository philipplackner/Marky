@file:OptIn(ExperimentalForeignApi::class)

package com.plcoding.marky.core.files

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.uikit.LocalUIViewController
import com.plcoding.marky.core.domain.util.Result
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.dataWithContentsOfURL
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerViewController
import platform.UniformTypeIdentifiers.UTType
import platform.UniformTypeIdentifiers.UTTypeItem
import platform.darwin.NSObject

@Composable
actual fun rememberFilePicker(
    type: FileType,
    onResult: (Result<PickedFile, FilePickError>) -> Unit
): FilePickerLauncher {
    val viewController = LocalUIViewController.current
    val scope = rememberCoroutineScope()
    val currentType by rememberUpdatedState(type)
    val currentOnResult by rememberUpdatedState(onResult)

    // UIDocumentPickerViewController holds its delegate weakly.
    val delegate = remember {
        PickerDelegate { url ->
            scope.launch {
                currentOnResult(readPickedFile(url, currentType))
            }
        }
    }

    return remember(viewController, delegate) {
        FilePickerLauncher {
            val contentTypes = currentType.extensions
                .mapNotNull { UTType.typeWithFilenameExtension(it) }
                .ifEmpty { listOf(UTTypeItem) }
            val picker = UIDocumentPickerViewController(forOpeningContentTypes = contentTypes, asCopy = true)
            picker.delegate = delegate
            picker.allowsMultipleSelection = false
            viewController.presentViewController(picker, animated = true, completion = null)
        }
    }
}

private class PickerDelegate(
    private val onPicked: (NSURL) -> Unit
) : NSObject(), UIDocumentPickerDelegateProtocol {

    override fun documentPicker(
        controller: UIDocumentPickerViewController,
        didPickDocumentsAtURLs: List<*>
    ) {
        (didPickDocumentsAtURLs.firstOrNull() as? NSURL)?.let(onPicked)
    }
}

private suspend fun readPickedFile(
    url: NSURL,
    type: FileType
): Result<PickedFile, FilePickError> = withContext(Dispatchers.IO) {
    val name = url.lastPathComponent.orEmpty()
    try {
        if (!type.matches(name)) {
            return@withContext Result.Error(FilePickError.WrongFileType(name))
        }
        val data = NSData.dataWithContentsOfURL(url)
            ?: return@withContext Result.Error(FilePickError.Unreadable(name))
        Result.Success(PickedFile(name, data.toByteArray()))
    } finally {
        NSFileManager.defaultManager.removeItemAtURL(url, error = null)
    }
}
