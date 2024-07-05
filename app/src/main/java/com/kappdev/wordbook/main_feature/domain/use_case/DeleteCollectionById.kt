package com.kappdev.wordbook.main_feature.domain.use_case

import com.kappdev.wordbook.core.domain.repository.CardRepository
import com.kappdev.wordbook.core.domain.repository.CollectionRepository
import javax.inject.Inject

class DeleteCollectionById @Inject constructor(
    private val collectionRepository: CollectionRepository,
    private val cardRepository: CardRepository
) {

    suspend operator fun invoke(collectionId: Int) {
        cardRepository.deleteCollectionCards(collectionId)
        collectionRepository.deleteCollectionById(collectionId)
    }
}