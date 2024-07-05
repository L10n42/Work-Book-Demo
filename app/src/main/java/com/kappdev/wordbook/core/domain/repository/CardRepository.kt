package com.kappdev.wordbook.core.domain.repository

import com.kappdev.wordbook.core.domain.model.Card
import com.kappdev.wordbook.main_feature.domain.model.TermDuplicate
import com.kappdev.wordbook.main_feature.domain.util.CardsOrder
import kotlinx.coroutines.flow.Flow

interface CardRepository {

    suspend fun insertCard(card: Card)

    suspend fun insertCards(cards: List<Card>)

    suspend fun getCardById(id: Int): Card?

    fun findDuplicates(term: String): List<TermDuplicate>

    fun getCollectionCards(collectionId: Int, searchArg: String, order: CardsOrder): Flow<List<Card>>

    fun getCollectionCards(collectionId: Int): List<Card>

    suspend fun deleteCardById(id: Int)

    suspend fun moveCardTo(cardId: Int, newCollectionId: Int)

    suspend fun deleteCollectionCards(collectionId: Int)
}