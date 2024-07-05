package com.kappdev.wordbook.main_feature.domain.use_case

import com.kappdev.wordbook.core.domain.repository.CardRepository
import javax.inject.Inject

class MoveCardTo @Inject constructor(
    private val cardRepository: CardRepository
) {

    suspend operator fun invoke(cardId: Int, newCollectionId: Int) {
        cardRepository.moveCardTo(cardId, newCollectionId)
    }
}