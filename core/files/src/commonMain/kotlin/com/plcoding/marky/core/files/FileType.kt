package com.plcoding.marky.core.files

/**
 * The kind of file a picker accepts.
 *
 * @property extensions file name extensions without the dot, e.g. `md`. A picked file must end in one
 * of them; the system filter alone isn't reliable. Empty means all files.
 * @property mimeTypes MIME types the Android picker filters by. Empty means all files.
 */
data class FileType(
    val extensions: List<String>,
    val mimeTypes: List<String>
)

fun FileType.matches(fileName: String): Boolean {
    if (extensions.isEmpty()) {
        return true
    }
    val extension = fileName.substringAfterLast('.', missingDelimiterValue = "")
    return extension.isNotEmpty() && extensions.any { it.equals(extension, ignoreCase = true) }
}
