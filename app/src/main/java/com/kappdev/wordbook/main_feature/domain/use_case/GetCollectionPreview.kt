package com.kappdev.wordbook.main_feature.domain.use_case

import com.kappdev.wordbook.core.domain.repository.CollectionRepository
import com.kappdev.wordbook.main_feature.domain.model.CollectionPreview
import javax.inject.Inject

class GetCollectionPreview @Inject constructor(
    private val collectionRepository: CollectionRepository
) {

    operator fun invoke(collectionId: Int): CollectionPreview? {
        return collectionRepository.getCollectionPreview(collectionId)
    }
}