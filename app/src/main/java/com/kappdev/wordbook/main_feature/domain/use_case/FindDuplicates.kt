package com.kappdev.wordbook.main_feature.domain.use_case

import com.kappdev.wordbook.core.domain.repository.CardRepository
import com.kappdev.wordbook.main_feature.domain.model.TermDuplicate
import javax.inject.Inject

class FindDuplicates @Inject constructor(
    private val cardRepository: CardRepository
) {

    operator fun invoke(term: String): List<TermDuplicate> {
        return cardRepository.findDuplicates(term)
    }
}