package com.kappdev.wordbook.main_feature.presentation.cards

import com.kappdev.wordbook.core.domain.model.Card

sealed class CardSheet {
    data class Options(val card: Card): CardSheet()
    data class Delete(val card: Card): CardSheet()
    data class Share(val card: Card): CardSheet()
    data class PickCollection(val cardId: Int): CardSheet()
    data object Order: CardSheet()
}
