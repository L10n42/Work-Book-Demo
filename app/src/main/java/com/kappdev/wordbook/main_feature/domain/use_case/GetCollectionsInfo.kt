package com.kappdev.wordbook.main_feature.domain.use_case

import com.kappdev.wordbook.core.domain.repository.CollectionRepository
import com.kappdev.wordbook.main_feature.domain.model.CollectionInfo
import com.kappdev.wordbook.main_feature.domain.util.CollectionsOrder
import com.kappdev.wordbook.main_feature.domain.util.OrderType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCollectionsInfo @Inject constructor(
    private val collectionRepository: CollectionRepository
) {

    operator fun invoke(
        searchArg: String = "",
        order: CollectionsOrder = CollectionsOrder.Name(OrderType.Ascending)
    ): Flow<List<CollectionInfo>> {
        return collectionRepository.getCollectionsInfo(searchArg, order)
    }
}