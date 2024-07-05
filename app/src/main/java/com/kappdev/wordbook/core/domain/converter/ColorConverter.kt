package com.kappdev.wordbook.core.domain.converter

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.room.TypeConverter

object ColorConverter {

    @TypeConverter
    fun fromColor(color: Color): Int {
        return color.toArgb()
    }

    @TypeConverter
    fun toColor(colorValue: Int): Color {
        return Color(colorValue)
    }
}