package com.kappdev.wordbook.study_feature.presentation.flashcards

import com.kappdev.wordbook.core.domain.model.Card

data class SwipeInfo(
    val card: Card,
    val direction: CardDragDirection
)
