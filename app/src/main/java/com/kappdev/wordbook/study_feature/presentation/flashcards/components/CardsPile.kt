package com.kappdev.wordbook.study_feature.presentation.flashcards.components

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.kappdev.wordbook.core.domain.model.Card
import com.kappdev.wordbook.study_feature.presentation.flashcards.CardDragDirection
import com.kappdev.wordbook.study_feature.presentation.flashcards.SwipeInfo
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

@Composable
fun CardsPile(
    cards: List<Card>,
    swipeCard: SharedFlow<SwipeInfo>,
    modifier: Modifier = Modifier,
    onRelearn: () -> Unit,
    onLearned: () -> Unit,
    onTermSpeak: (text: String) -> Unit,
    onDefinitionSpeak: (text: String) -> Unit,
) {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    Box(modifier) {
        val cardsChunk = cards.take(3).reversed()
        cardsChunk.forEachIndexed { index, card ->
            key(card) {
                val cardRotation by animateFloatAsState(
                    targetValue = when {
                        (index == cardsChunk.lastIndex - 1) -> 3f
                        (index == cardsChunk.lastIndex - 2) -> -3f
                        else -> 0f
                    },
                    animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing),
                    label = "CardRotationAnimation"
                )

                Flashcard(
                    modifier = Modifier.matchParentSize().rotate(cardRotation),
                    swipeCard = swipeCard.filter { it.card == card }.map { it.direction },
                    enabled = (index == cardsChunk.lastIndex),
                    onSwipe = { swipeDirection ->
                        when (swipeDirection) {
                            CardDragDirection.Right -> if (isRtl) onRelearn() else onLearned()
                            CardDragDirection.Left ->  if (isRtl) onLearned() else onRelearn()
                        }
                    },
                    backSide = {
                        BackSideCard(
                            imagePath = card.image,
                            modifier = Modifier
                                .matchParentSize()
                                .padding(16.dp),
                            definition = card.definition,
                            example = card.example,
                            onSpeak = onDefinitionSpeak
                        )
                    },
                    frontSide = {
                        FrontSizeCard(
                            term = card.term,
                            modifier = Modifier
                                .matchParentSize()
                                .padding(16.dp),
                            transcription = card.transcription,
                            onSpeak = onTermSpeak
                        )
                    }
                )
            }
        }
    }
}