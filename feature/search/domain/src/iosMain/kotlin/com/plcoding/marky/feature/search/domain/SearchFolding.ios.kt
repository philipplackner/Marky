package com.plcoding.marky.feature.search.domain

import platform.Foundation.NSString
import platform.Foundation.decomposedStringWithCanonicalMapping

@Suppress("CAST_NEVER_SUCCEEDS")
internal actual fun decompose(char: Char): String =
    (char.toString() as NSString).decomposedStringWithCanonicalMapping
