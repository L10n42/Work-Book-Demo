package com.kappdev.wordbook.study_feature.domain.model

import androidx.room.ColumnInfo
import java.util.Locale

data class CollectionLanguages(
    @ColumnInfo(name = "term_language")
    val term: Locale,

    @ColumnInfo(name = "definition_language")
    val definition: Locale,
)
