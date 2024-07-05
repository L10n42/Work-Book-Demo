package com.kappdev.wordbook.core.domain.converter

import androidx.room.TypeConverter
import java.util.Locale

object LocaleConverter {

    @TypeConverter
    fun fromLocale(locale: Locale): String {
        return locale.toString()
    }

    @TypeConverter
    fun toLocale(localeString: String): Locale {
        val parts = localeString.split("_")
        return when (parts.size) {
            1 -> Locale(parts[0])
            2 -> Locale(parts[0], parts[1])
            else -> Locale(parts[0], parts[1], parts[2])
        }
    }
}