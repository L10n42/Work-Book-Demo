package com.kappdev.wordbook.main_feature.domain.use_case

import com.kappdev.wordbook.core.domain.repository.CollectionRepository
import com.kappdev.wordbook.main_feature.domain.model.CollectionPreview
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCollectionsPreview @Inject constructor(
    private val collectionRepository: CollectionRepository
) {

    operator fun invoke(): Flow<List<CollectionPreview>> {
        return collectionRepository.getCollectionsPreview()
    }
}