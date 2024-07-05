package com.kappdev.wordbook.main_feature.domain.model

import androidx.room.ColumnInfo

data class TermDuplicate(
    @ColumnInfo(name = "term")
    val term: String,

    @ColumnInfo(name = "definition")
    val definition: String,

    @ColumnInfo(name = "collection_name")
    val collectionName: String
)