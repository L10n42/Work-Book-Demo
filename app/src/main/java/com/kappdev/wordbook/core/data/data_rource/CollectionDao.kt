package com.kappdev.wordbook.core.data.data_rource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kappdev.wordbook.core.domain.model.Collection
import com.kappdev.wordbook.main_feature.domain.model.CollectionInfo
import com.kappdev.wordbook.main_feature.domain.model.CollectionPreview
import com.kappdev.wordbook.share_feature.domain.model.CollectionPDFInfo
import com.kappdev.wordbook.study_feature.domain.model.CollectionLanguages
import kotlinx.coroutines.flow.Flow
import java.util.Locale

@Dao
interface CollectionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollection(collection: Collection): Long

    @Query("SELECT * FROM collections WHERE collection_id = :id LIMIT 1")
    suspend fun getCollectionById(id: Int): Collection?

    @Query("DELETE FROM collections WHERE collection_id = :id")
    suspend fun deleteCollectionById(id: Int)

    @Query("SELECT name FROM collections WHERE collection_id = :id LIMIT 1")
    suspend fun getCollectionName(id: Int): String?

    @Query("SELECT term_language FROM collections WHERE collection_id = :id LIMIT 1")
    suspend fun getCollectionLanguage(id: Int): Locale?

    @Query("SELECT term_language, definition_language FROM collections WHERE collection_id = :id LIMIT 1")
    suspend fun getCollectionLanguages(id: Int): CollectionLanguages?

    @Query("SELECT COUNT(*) FROM collections")
    fun getItemsCount(): Int

    @Query("""
        SELECT c.collection_id, c.name, c.description, COUNT(card.card_id) AS cards_count, c.card_color, c.background_image
        FROM collections c
        LEFT JOIN cards card ON c.collection_id = card.collection_id
        WHERE c.name LIKE '%' || :searchArg || '%' OR c.description LIKE '%' || :searchArg || '%'
        GROUP BY c.collection_id
        ORDER BY
        CASE
            WHEN :orderKey = 'name' THEN c.name
            WHEN :orderKey = 'cards_count' THEN cards_count
            WHEN :orderKey = 'created' THEN c.created
            WHEN :orderKey = 'last_edit' THEN c.last_edit
        END ASC
    """)
    fun getCollectionsInfo(searchArg: String, orderKey: String): Flow<List<CollectionInfo>>

    @Query("""
        SELECT c.collection_id, c.name, c.description, COUNT(card.card_id) AS cards_count, c.card_color, c.background_image
        FROM collections c
        LEFT JOIN cards card ON c.collection_id = card.collection_id
        WHERE c.name LIKE '%' || :searchArg || '%' OR c.description LIKE '%' || :searchArg || '%'
        GROUP BY c.collection_id
        ORDER BY
        CASE
            WHEN :orderKey = 'name' THEN c.name
            WHEN :orderKey = 'cards_count' THEN cards_count
            WHEN :orderKey = 'created' THEN c.created
            WHEN :orderKey = 'last_edit' THEN c.last_edit
        END DESC
    """)
    fun getCollectionsInfoDesc(searchArg: String, orderKey: String): Flow<List<CollectionInfo>>

    @Query("""
        SELECT c.name, c.description, COUNT(card.card_id) AS cards_count
        FROM collections c
        LEFT JOIN cards card ON c.collection_id = card.collection_id
        WHERE c.collection_id = :id
    """)
    fun getCollectionPDFInfo(id: Int): CollectionPDFInfo

    @Query("""
        SELECT 
        c.collection_id AS id,
        c.name,
        COUNT(card.card_id) AS cardsCount
        FROM collections c
        LEFT JOIN cards card ON c.collection_id = card.collection_id 
        GROUP BY c.collection_id
    """)
    fun getCollectionsPreview(): Flow<List<CollectionPreview>>

    @Query("""
        SELECT 
        c.collection_id AS id,
        c.name,
        COUNT(card.card_id) AS cardsCount
        FROM collections c
        LEFT JOIN cards card ON c.collection_id = card.collection_id
        WHERE c.collection_id = :id
        GROUP BY c.collection_id
    """)
    fun getCollectionPreview(id: Int): CollectionPreview?

}