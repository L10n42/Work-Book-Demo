package com.kappdev.wordbook.main_feature.domain.use_case

import com.kappdev.wordbook.core.domain.model.Card
import com.kappdev.wordbook.core.domain.repository.CardRepository
import com.kappdev.wordbook.main_feature.domain.util.CardsOrder
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCollectionCards @Inject constructor(
    private val cardRepository: CardRepository
) {

    operator fun invoke(
        collectionId: Int,
        searchArg: String = "",
        order: CardsOrder = CardsOrder.Term()
    ): Flow<List<Card>> {
        return cardRepository.getCollectionCards(collectionId, searchArg, order)
    }
}