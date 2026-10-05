package com.plcoding.marky.core.files

private const val MAX_FILE_NAME_BYTES = 255
private const val REPLACEMENT = '_'
private val RESERVED_CHARS = setOf('/', '\\', ':', '*', '?', '"', '<', '>', '|')

/**
 * Makes [name] safe to use as a file name on Android and iOS file providers: replaces reserved and
 * control characters with `_`, trims whitespace and shortens the part before the extension so the
 * name fits in 255 UTF-8 bytes.
 */
fun sanitizeFileName(name: String): String {
    val cleaned = buildString {
        name.trim().forEach { char ->
            append(if (char in RESERVED_CHARS || char.isISOControl()) REPLACEMENT else char)
        }
    }

    if (cleaned.encodeToByteArray().size <= MAX_FILE_NAME_BYTES) {
        return cleaned
    }

    val dotIndex = cleaned.lastIndexOf('.')
    val extension = if (dotIndex > 0) cleaned.substring(dotIndex) else ""
    val base = cleaned.removeSuffix(extension)
    val maxBaseBytes = MAX_FILE_NAME_BYTES - extension.encodeToByteArray().size
    return base.takeUtf8Bytes(maxBaseBytes).trimEnd() + extension
}

private fun String.takeUtf8Bytes(maxBytes: Int): String {
    var bytes = 0
    var end = 0
    while (end < length) {
        val charCount = if (this[end].isHighSurrogate() && end + 1 < length) 2 else 1
        val charBytes = substring(end, end + charCount).encodeToByteArray().size
        if (bytes + charBytes > maxBytes) {
            break
        }
        bytes += charBytes
        end += charCount
    }
    return substring(0, end)
}
