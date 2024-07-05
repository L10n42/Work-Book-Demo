package com.kappdev.wordbook.core.data.data_rource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kappdev.wordbook.core.domain.model.Card
import com.kappdev.wordbook.main_feature.domain.model.TermDuplicate
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: Card)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCards(cards: List<Card>)

    @Query("SELECT * FROM cards WHERE card_id = :id LIMIT 1")
    suspend fun getCardById(id: Int): Card?

    @Query("DELETE FROM cards WHERE collection_id = :collectionId")
    suspend fun deleteCollectionCards(collectionId: Int)

    @Query("DELETE FROM cards WHERE card_id = :id")
    suspend fun deleteCardById(id: Int)

    @Query("UPDATE cards SET collection_id = :newCollectionId WHERE card_id = :cardId")
    suspend fun moveCardTo(cardId: Int, newCollectionId: Int)

    @Query("""
        SELECT card.term, card.definition, c.name AS collection_name
        FROM cards card
        JOIN collections c ON card.collection_id = c.collection_id
        WHERE card.term = :term COLLATE NOCASE
    """)
    fun findDuplicates(term: String): List<TermDuplicate>

    @Query("""
        SELECT * FROM cards
        WHERE collection_id = :collectionId AND
        (term LIKE '%' || :searchArg || '%' OR 
        definition LIKE '%' || :searchArg || '%' OR
        example LIKE '%' || :searchArg || '%')
        ORDER BY CASE
            WHEN :orderKey = 'term' THEN term
            WHEN :orderKey = 'created' THEN created
            WHEN :orderKey = 'last_edit' THEN last_edit
        END ASC
    """)
    fun getCollectionCards(collectionId: Int, searchArg: String, orderKey: String): Flow<List<Card>>

    @Query("""
        SELECT * FROM cards
        WHERE collection_id = :collectionId AND
        (term LIKE '%' || :searchArg || '%' OR 
        definition LIKE '%' || :searchArg || '%' OR
        example LIKE '%' || :searchArg || '%')
        ORDER BY CASE
            WHEN :orderKey = 'term' THEN term
            WHEN :orderKey = 'created' THEN created
            WHEN :orderKey = 'last_edit' THEN last_edit
        END Desc
    """)
    fun getCollectionCardsDesc(collectionId: Int, searchArg: String, orderKey: String): Flow<List<Card>>

    @Query("SELECT * FROM cards WHERE collection_id = :collectionId")
    fun getCollectionCards(collectionId: Int): List<Card>

}