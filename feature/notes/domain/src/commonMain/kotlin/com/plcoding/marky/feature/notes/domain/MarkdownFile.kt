package com.plcoding.marky.feature.notes.domain

/**
 * The title of a note imported from [fileName]: the file name without its `.md` extension.
 */
fun noteTitleFromFileName(fileName: String): String {
    val name = fileName.trim()
    val withoutExtension = if (name.endsWith(MARKDOWN_EXTENSION, ignoreCase = true)) {
        name.dropLast(MARKDOWN_EXTENSION.length)
    } else {
        name
    }
    return withoutExtension.trim()
}

/**
 * The text of an imported `.md` file, or null if [bytes] aren't UTF-8 text. A NUL character
 * counts as binary data, since text files never contain one.
 */
fun decodeMarkdownFile(bytes: ByteArray): String? {
    val text = try {
        bytes.decodeToString(throwOnInvalidSequence = true)
    } catch (_: CharacterCodingException) {
        return null
    }
    if ('\u0000' in text) {
        return null
    }
    return text.removePrefix(BYTE_ORDER_MARK)
}

/**
 * The bytes of a note exported as a `.md` file: its text as UTF-8.
 */
fun encodeMarkdownFile(text: String): ByteArray = text.encodeToByteArray()

/**
 * The name of the `.md` file a note is exported as, or null if the note has no title.
 */
fun exportFileName(title: String): String? = title.trim().ifEmpty { null }?.let { it + MARKDOWN_EXTENSION }

const val MARKDOWN_EXTENSION = ".md"
const val MARKDOWN_MIME_TYPE = "text/markdown"
private const val BYTE_ORDER_MARK = "﻿"
