package com.kappdev.wordbook.main_feature.domain.use_case

import com.kappdev.wordbook.core.domain.repository.CardRepository
import javax.inject.Inject

class DeleteCardById @Inject constructor(
    private val cardRepository: CardRepository
) {

    suspend operator fun invoke(cardId: Int) {
        cardRepository.deleteCardById(cardId)
    }
}