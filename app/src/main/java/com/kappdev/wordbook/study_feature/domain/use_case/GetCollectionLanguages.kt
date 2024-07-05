package com.kappdev.wordbook.study_feature.domain.use_case

import com.kappdev.wordbook.core.domain.repository.CollectionRepository
import com.kappdev.wordbook.study_feature.domain.model.CollectionLanguages
import javax.inject.Inject

class GetCollectionLanguages @Inject constructor(
    private val collectionRepository: CollectionRepository
) {

    suspend operator fun invoke(collectionId: Int): CollectionLanguages? {
        return collectionRepository.getCollectionLanguages(collectionId)
    }
}