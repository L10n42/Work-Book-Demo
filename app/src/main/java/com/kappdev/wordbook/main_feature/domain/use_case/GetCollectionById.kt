package com.kappdev.wordbook.main_feature.domain.use_case

import com.kappdev.wordbook.core.domain.model.Collection
import com.kappdev.wordbook.core.domain.repository.CollectionRepository
import javax.inject.Inject

class GetCollectionById @Inject constructor(
    private val collectionRepository: CollectionRepository
) {

    suspend operator fun invoke(id: Int): Collection? {
        return collectionRepository.getCollectionById(id)
    }
}