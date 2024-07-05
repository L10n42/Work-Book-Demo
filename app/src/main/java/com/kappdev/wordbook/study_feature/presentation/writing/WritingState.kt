package com.kappdev.wordbook.study_feature.presentation.writing

import com.kappdev.wordbook.core.domain.model.Card
import com.kappdev.wordbook.study_feature.presentation.common.AnswerState

data class WritingState(
    val id: Int,
    val question: String,
    val correctAnswer: String,
    val answer: String,
    val answerState: AnswerState,
    val showAnswer: Boolean
) { companion object }

fun WritingState.Companion.from(card: Card): WritingState {
    return WritingState(
        id = card.id,
        question = card.definition,
        correctAnswer = card.term,
        answerState = AnswerState.Unchecked,
        showAnswer = false,
        answer = ""
    )
}

fun WritingState.showAnswer(): WritingState {
    return this.copy(showAnswer = true)
}

fun WritingState.answer(): WritingState {
    val isAnswerCorrect = answer.trim().equals(correctAnswer.trim(), ignoreCase = true)
    return this.copy(
        answerState = if (isAnswerCorrect) AnswerState.Correct else AnswerState.Wrong
    )
}
