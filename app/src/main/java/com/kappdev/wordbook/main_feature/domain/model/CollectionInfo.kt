package com.kappdev.wordbook.main_feature.domain.model

import androidx.compose.ui.graphics.Color
import androidx.room.ColumnInfo

data class CollectionInfo(
    @ColumnInfo(name = "collection_id")
    val id: Int,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "cards_count")
    val cardsCount: Int,

    @ColumnInfo(name = "card_color")
    val color: Color? = null,

    @ColumnInfo(name = "background_image")
    val backgroundImage: String? = null
)
