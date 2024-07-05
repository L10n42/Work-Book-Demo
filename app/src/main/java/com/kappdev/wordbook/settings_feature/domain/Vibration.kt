package com.kappdev.wordbook.settings_feature.domain

enum class Vibration {
    Allowed, Denied
}

fun Vibration.isAllowed() = (this == Vibration.Allowed)