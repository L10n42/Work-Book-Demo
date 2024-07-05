package com.kappdev.wordbook.core.data.repository

import com.kappdev.wordbook.core.data.data_rource.CollectionDao
import com.kappdev.wordbook.core.domain.model.Collection
import com.kappdev.wordbook.core.domain.repository.CollectionRepository
import com.kappdev.wordbook.main_feature.domain.model.CollectionInfo
import com.kappdev.wordbook.main_feature.domain.model.CollectionPreview
import com.kappdev.wordbook.main_feature.domain.util.CollectionsOrder
import com.kappdev.wordbook.main_feature.domain.util.OrderType
import com.kappdev.wordbook.share_feature.domain.model.CollectionPDFInfo
import com.kappdev.wordbook.study_feature.domain.model.CollectionLanguages
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.util.Locale
import javax.inject.Inject

class CollectionRepositoryImpl @Inject constructor(
    private val collectionDao: CollectionDao
) : CollectionRepository {

    override suspend fun insertCollection(collection: Collection): Long {
        return collectionDao.insertCollection(collection)
    }

    override suspend fun deleteCollectionById(id: Int) {
        collectionDao.getCollectionById(id)?.let { collection ->
            collection.backgroundImage?.let { path -> File(path).delete() }
        }
        collectionDao.deleteCollectionById(id)
    }

    override suspend fun getCollectionById(id: Int): Collection? {
        return collectionDao.getCollectionById(id)
    }

    override suspend fun getCollectionName(id: Int): String? {
        return collectionDao.getCollectionName(id)
    }

    override suspend fun getCollectionLanguage(id: Int): Locale? {
        return collectionDao.getCollectionLanguage(id)
    }

    override suspend fun getCollectionLanguages(id: Int): CollectionLanguages? {
        return collectionDao.getCollectionLanguages(id)
    }

    override fun hasData(): Boolean {
        return collectionDao.getItemsCount() > 0
    }

    override fun getCollectionsInfo(searchArg: String, order: CollectionsOrder): Flow<List<CollectionInfo>> {
        return when (order.type) {
            OrderType.Ascending -> collectionDao.getCollectionsInfo(searchArg, order.key)
            OrderType.Descending -> collectionDao.getCollectionsInfoDesc(searchArg, order.key)
        }
    }

    override fun getCollectionPDFInfo(id: Int): CollectionPDFInfo {
        return collectionDao.getCollectionPDFInfo(id)
    }

    override fun getCollectionsPreview(): Flow<List<CollectionPreview>> {
        return collectionDao.getCollectionsPreview()
    }

    override fun getCollectionPreview(id: Int): CollectionPreview? {
        return collectionDao.getCollectionPreview(id)
    }
}