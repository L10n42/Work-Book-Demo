package com.kappdev.wordbook.core.domain.model

import androidx.compose.ui.graphics.Color
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.kappdev.wordbook.core.domain.converter.ColorConverter
import com.kappdev.wordbook.core.domain.converter.LocaleConverter
import java.util.Locale

@Entity(tableName = "collections")
data class Collection(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "collection_id")
    val id: Int = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "term_language")
    @TypeConverters(LocaleConverter::class)
    val termLanguage: Locale,

    @ColumnInfo(name = "definition_language")
    @TypeConverters(LocaleConverter::class)
    val definitionLanguage: Locale,

    @ColumnInfo(name = "background_image")
    val backgroundImage: String?,

    @ColumnInfo(name = "card_color")
    @TypeConverters(ColorConverter::class)
    val color: Color?,

    @ColumnInfo(name = "created", defaultValue = "0")
    val created: Long,

    @ColumnInfo(name = "last_edit", defaultValue = "0")
    val lastEdit: Long
)
