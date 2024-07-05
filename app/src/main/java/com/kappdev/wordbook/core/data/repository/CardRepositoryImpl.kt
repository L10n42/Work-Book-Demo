package com.kappdev.wordbook.core.data.repository

import com.kappdev.wordbook.core.data.data_rource.CardDao
import com.kappdev.wordbook.core.domain.model.Card
import com.kappdev.wordbook.core.domain.repository.CardRepository
import com.kappdev.wordbook.main_feature.domain.model.TermDuplicate
import com.kappdev.wordbook.main_feature.domain.util.CardsOrder
import com.kappdev.wordbook.main_feature.domain.util.OrderType
import kotlinx.coroutines.flow.Flow
import java.io.File
import javax.inject.Inject

class CardRepositoryImpl @Inject constructor(
    private val cardDao: CardDao
) : CardRepository {

    override suspend fun insertCard(card: Card) {
        cardDao.insertCard(card)
    }

    override suspend fun insertCards(cards: List<Card>) {
        cardDao.insertCards(cards)
    }

    override suspend fun getCardById(id: Int): Card? {
        return cardDao.getCardById(id)
    }

    override fun findDuplicates(term: String): List<TermDuplicate> {
        return cardDao.findDuplicates(term)
    }

    override fun getCollectionCards(collectionId: Int, searchArg: String, order: CardsOrder): Flow<List<Card>> {
        return when (order.type) {
            OrderType.Ascending -> cardDao.getCollectionCards(collectionId, searchArg, order.key)
            OrderType.Descending -> cardDao.getCollectionCardsDesc(collectionId, searchArg, order.key)
        }
    }

    override fun getCollectionCards(collectionId: Int): List<Card> {
        return cardDao.getCollectionCards(collectionId)
    }

    override suspend fun deleteCardById(id: Int) {
        cardDao.getCardById(id)?.let { card ->
            card.image?.let { path -> File(path).delete() }
        }
        cardDao.deleteCardById(id)
    }

    override suspend fun moveCardTo(cardId: Int, newCollectionId: Int) {
        cardDao.moveCardTo(cardId, newCollectionId)
    }

    override suspend fun deleteCollectionCards(collectionId: Int) {
        cardDao.getCollectionCards(collectionId).forEach { card ->
            card.image?.let { path -> File(path).delete() }
        }
        cardDao.deleteCollectionCards(collectionId)
    }

}