package com.kappdev.wordbook.core.domain.repository

import com.kappdev.wordbook.core.domain.model.Collection
import com.kappdev.wordbook.main_feature.domain.model.CollectionInfo
import com.kappdev.wordbook.main_feature.domain.model.CollectionPreview
import com.kappdev.wordbook.main_feature.domain.util.CollectionsOrder
import com.kappdev.wordbook.share_feature.domain.model.CollectionPDFInfo
import com.kappdev.wordbook.study_feature.domain.model.CollectionLanguages
import kotlinx.coroutines.flow.Flow
import java.util.Locale

interface CollectionRepository {

    suspend fun insertCollection(collection: Collection): Long

    suspend fun getCollectionById(id: Int): Collection?

    suspend fun getCollectionName(id: Int): String?

    suspend fun getCollectionLanguage(id: Int): Locale?

    suspend fun getCollectionLanguages(id: Int): CollectionLanguages?

    /**
     * Determines whether the collections table has any data.
     * @return true if in the collections table is at least one collection, false if not.
     * */
    fun hasData(): Boolean

    fun getCollectionsInfo(searchArg: String, order: CollectionsOrder): Flow<List<CollectionInfo>>

    fun getCollectionPDFInfo(id: Int): CollectionPDFInfo

    fun getCollectionsPreview(): Flow<List<CollectionPreview>>

    fun getCollectionPreview(id: Int): CollectionPreview?

    suspend fun deleteCollectionById(id: Int)

}