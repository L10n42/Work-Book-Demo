package com.kappdev.wordbook.main_feature.domain.use_case

import com.kappdev.wordbook.core.domain.model.Card
import com.kappdev.wordbook.core.domain.repository.CardRepository
import javax.inject.Inject

class GetCardById @Inject constructor(
    private val cardRepository: CardRepository
) {

    suspend operator fun invoke(id: Int): Card? {
        return cardRepository.getCardById(id)
    }
}