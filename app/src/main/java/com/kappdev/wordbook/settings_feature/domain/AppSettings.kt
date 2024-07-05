package com.kappdev.wordbook.settings_feature.domain

import androidx.compose.runtime.staticCompositionLocalOf

data class AppSettings(
    val theme: Theme = Settings.Theme.default,
    val vibration: Vibration = Settings.Vibration.default,
    val progressBarStyle: ProgressBarStyle = Settings.ProgressBarStyle.default,
    val checkForDuplication: Boolean = Settings.CheckForDuplication.default,
    val capitalizeSentences: Boolean = Settings.CapitalizeSentences.default
)

internal val LocalAppSettings = staticCompositionLocalOf { AppSettings() }