package com.kappdev.wordbook.main_feature.presentation.collections

import com.kappdev.wordbook.main_feature.domain.model.CollectionInfo

sealed class CollectionSheet {
    data class Options(val collection: CollectionInfo): CollectionSheet()
    data class Delete(val collection: CollectionInfo): CollectionSheet()
    data object Order: CollectionSheet()
}
