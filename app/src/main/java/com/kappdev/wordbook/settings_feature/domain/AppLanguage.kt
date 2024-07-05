package com.kappdev.wordbook.settings_feature.domain

import java.util.Locale

enum class AppLanguage(val locale: Locale, val nameToDisplay: String, val flag: String) {
    ENGLISH(Locale.ENGLISH, "English", "\uD83C\uDDEC\uD83C\uDDE7"),
    UKRAINIAN(Locale("uk"), "Українська", "\uD83C\uDDFA\uD83C\uDDE6"),
    GERMAN(Locale.GERMAN, "Deutsch", "\uD83C\uDDE9\uD83C\uDDEA"),
    SPANISH(Locale("es"), "Español", "\uD83C\uDDEA\uD83C\uDDF8"),
    FRENCH(Locale.FRENCH, "Français", "\uD83C\uDDEB\uD83C\uDDF7"),
    PORTUGUESE(Locale("pt"), "Português", "\uD83C\uDDF5\uD83C\uDDF9"),
    INDONESIAN(Locale("in"), "Indonesia", "\uD83C\uDDEE\uD83C\uDDE9"),
    ARABIC(Locale("ar"), "العربية", "\uD83C\uDDF8\uD83C\uDDE6"),
    HINDI(Locale("hi"), "हिन्दी", "\uD83C\uDDEE\uD83C\uDDF3");

    companion object {
        fun getByLocale(locale: Locale): AppLanguage? {
            return entries.find { it.locale == locale }
        }
    }
}