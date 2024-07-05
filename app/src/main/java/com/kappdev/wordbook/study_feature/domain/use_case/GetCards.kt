package com.kappdev.wordbook.study_feature.domain.use_case

import com.kappdev.wordbook.core.domain.model.Card
import com.kappdev.wordbook.core.domain.repository.CardRepository
import javax.inject.Inject

class GetCards @Inject constructor(
    private val cardRepository: CardRepository
) {

    operator fun invoke(collectionId: Int): List<Card> {
        return cardRepository.getCollectionCards(collectionId)
    }
}