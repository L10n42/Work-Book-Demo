package com.kappdev.wordbook.settings_feature.domain

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.kappdev.wordbook.settings_feature.domain.converters.EnumConverter
import com.kappdev.wordbook.settings_feature.domain.converters.LinearConverter
import com.kappdev.wordbook.settings_feature.domain.converters.SettingsConverter
import com.kappdev.wordbook.settings_feature.domain.Reminder as UsageReminder
import com.kappdev.wordbook.settings_feature.domain.Theme as AppTheme
import com.kappdev.wordbook.settings_feature.domain.Vibration as AppVibration

sealed class Settings<V, P>(val key: Preferences.Key<P>, val default: V, val converter: SettingsConverter<V, P>) {
    data object Theme : Settings<AppTheme, String>(
        stringPreferencesKey("THEME"),
        AppTheme.SystemDefault,
        EnumConverter(AppTheme::class.java)
    )

    data object Vibration : Settings<AppVibration, String>(
        stringPreferencesKey("VIBRATION"),
        default = AppVibration.Allowed,
        EnumConverter(AppVibration::class.java)
    )

    data object Reminder : Settings<UsageReminder, String>(
        stringPreferencesKey("REMINDER"),
        default = UsageReminder.Allowed,
        EnumConverter(UsageReminder::class.java)
    )

    data object ProgressBarStyle : Settings<com.kappdev.wordbook.settings_feature.domain.ProgressBarStyle, String>(
        stringPreferencesKey("PROGRESS_BAR_STYLE"),
        default = com.kappdev.wordbook.settings_feature.domain.ProgressBarStyle.Advanced,
        EnumConverter(com.kappdev.wordbook.settings_feature.domain.ProgressBarStyle::class.java)
    )

    data object CheckForDuplication : Settings<Boolean, Boolean>(
        booleanPreferencesKey("CHECK_FOR_DUPLICATION"),
        default = true,
        LinearConverter()
    )

    data object CapitalizeSentences : Settings<Boolean, Boolean>(
        booleanPreferencesKey("CAPITALIZE_SENTENCES"),
        default = true,
        LinearConverter()
    )
}