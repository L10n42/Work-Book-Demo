package com.kappdev.wordbook.share_feature.domain.model

import androidx.room.ColumnInfo

data class CollectionPDFInfo(
    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "cards_count")
    val cardsCount: Int
)