package com.plcoding.marky.feature.search.domain

import java.text.Normalizer

internal actual fun decompose(char: Char): String =
    Normalizer.normalize(char.toString(), Normalizer.Form.NFD)
