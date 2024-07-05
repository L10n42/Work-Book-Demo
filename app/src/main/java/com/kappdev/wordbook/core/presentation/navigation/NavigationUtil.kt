package com.kappdev.wordbook.core.presentation.navigation

fun String.putArg(name: String, value: Any?, optional: Boolean = false): String {
    val prefix = if (optional) "?" else ""
    return this.plus("$prefix$name=$value")
}

