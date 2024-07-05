package com.kappdev.wordbook.main_feature.domain.use_case

import com.kappdev.wordbook.core.domain.repository.CollectionRepository
import javax.inject.Inject

class GetCollectionName @Inject constructor(
    private val collectionRepository: CollectionRepository
) {

    suspend operator fun invoke(collectionId: Int): String? {
        return collectionRepository.getCollectionName(collectionId)
    }
}