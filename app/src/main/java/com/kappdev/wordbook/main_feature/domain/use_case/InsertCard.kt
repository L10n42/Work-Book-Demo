package com.kappdev.wordbook.main_feature.domain.use_case

import com.kappdev.wordbook.R
import com.kappdev.wordbook.core.domain.model.Card
import com.kappdev.wordbook.core.domain.repository.CardRepository
import com.kappdev.wordbook.core.domain.util.Result
import com.kappdev.wordbook.main_feature.domain.repository.StorageRepository
import com.kappdev.wordbook.main_feature.domain.util.Image
import javax.inject.Inject

class InsertCard @Inject constructor(
    private val cardRepository: CardRepository,
    private val storageRepository: StorageRepository
) {

    suspend operator fun invoke(card: Card, image: Image): Result<Unit> {
        validateValues(card)?.let { return it }

        val imageResult = manageImage(image)
        when (imageResult) {
            is Result.Failure -> return Result.Failure(imageResult.messageResId)
            is Result.Success -> cardRepository.insertCard(card.copy(image = imageResult.value))
        }

        return Result.Success(Unit)
    }

    private fun validateValues(card: Card): Result.Failure? {
        return when {
            card.term.isBlank() -> Result.Failure(R.string.enter_term_error)
            card.definition.isBlank() -> Result.Failure(R.string.enter_definition_error)
            else -> null
        }
    }

    private fun manageImage(image: Image): Result<String?> {
        return when (image) {
            is Image.Deleted -> {
                storageRepository.deleteImage(image.path)
                Result.Success(null)
            }
            is Image.Replaced -> {
                storageRepository.deleteImage(image.oldPath)
                storageRepository.storeImage(image.newUri)
            }
            is Image.NotStored -> storageRepository.storeImage(image.uri)
            is Image.Stored -> Result.Success(image.path)
            is Image.Empty -> Result.Success(null)
        }
    }
}