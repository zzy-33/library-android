package com.zzy.ktx


fun String?.ifNullOrEmpty(defaultValue: () -> String) = if (isNullOrEmpty()) defaultValue() else this

fun String?.ifNullOrBlank(defaultValue: () -> String) = if (isNullOrBlank()) defaultValue() else this

fun String.notContains(other: CharSequence, ignoreCase: Boolean = false) = !contains(other, ignoreCase)

fun String.notContains(char: Char, ignoreCase: Boolean = false) = !contains(char, ignoreCase)

fun String.notContains(regex: Regex) = !contains(regex)

fun String.unicodeToString(): String = runCatching {
    Regex("\\\\u(\\p{XDigit}{4})").replace(this) { matchResult ->
        matchResult.groupValues[1].toInt(16).toChar().toString()
    }
}.getOrDefault(this)

