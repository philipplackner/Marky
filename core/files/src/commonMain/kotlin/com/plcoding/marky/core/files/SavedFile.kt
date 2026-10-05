package com.plcoding.marky.core.files

/**
 * A file written to the location the user chose.
 *
 * @property name the final file name, which can differ from the suggested one if the user renamed it.
 */
data class SavedFile(val name: String)
