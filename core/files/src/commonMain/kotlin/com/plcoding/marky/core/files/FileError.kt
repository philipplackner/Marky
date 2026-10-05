package com.plcoding.marky.core.files

import com.plcoding.marky.core.domain.util.Error

sealed interface FilePickError : Error {
    val fileName: String

    data class WrongFileType(override val fileName: String) : FilePickError
    data class Unreadable(override val fileName: String) : FilePickError
}

sealed interface FileSaveError : Error {
    val fileName: String

    data class WriteFailed(override val fileName: String) : FileSaveError
}
