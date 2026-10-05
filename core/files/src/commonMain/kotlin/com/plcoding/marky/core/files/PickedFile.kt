package com.plcoding.marky.core.files

/**
 * A file the user picked, read fully into memory.
 *
 * @property name the file name including its extension.
 */
class PickedFile(
    val name: String,
    val bytes: ByteArray
)
