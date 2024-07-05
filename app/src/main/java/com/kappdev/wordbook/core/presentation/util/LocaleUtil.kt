package com.kappdev.wordbook.core.presentation.util

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

fun changeAppLocale(newLocale: Locale) {
    AppCompatDelegate.setApplicationLocales(
        LocaleListCompat.create(newLocale)
    )
}

fun getCurrentAppLocale(): Locale? {
    return AppCompatDelegate.getApplicationLocales().get(0)
}