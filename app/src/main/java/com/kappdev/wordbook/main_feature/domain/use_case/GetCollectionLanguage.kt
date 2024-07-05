package com.kappdev.wordbook.main_feature.domain.use_case

import com.kappdev.wordbook.core.domain.repository.CollectionRepository
import java.util.Locale
import javax.inject.Inject

class GetCollectionLanguage @Inject constructor(
    private val collectionRepository: CollectionRepository
) {

    suspend operator fun invoke(collectionId: Int): Locale? {
        return collectionRepository.getCollectionLanguage(collectionId)
    }
}