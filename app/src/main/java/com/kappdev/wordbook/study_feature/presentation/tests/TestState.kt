package com.kappdev.wordbook.study_feature.presentation.tests

import com.kappdev.wordbook.core.domain.model.Card
import com.kappdev.wordbook.study_feature.presentation.common.AnswerState

data class TestState(
    val id: Int,
    val question: String,
    val answers: List<Answer>,
    val isAnswered: Boolean
) { companion object }

fun TestState.Companion.from(questionCard: Card, answerCards: List<Card>): TestState {
    return TestState(
        id = questionCard.id,
        question = questionCard.definition,
        answers = answerCards.map { Answer(it.id, it.term, AnswerState.Unchecked) },
        isAnswered = false
    )
}

fun TestState.answer(index: Int): TestState {
    if (index !in this.answers.indices) throw IndexOutOfBoundsException()

    val newAnswerState = if (this.answers[index].id == this.id) AnswerState.Correct else AnswerState.Wrong
    val newAnswers = this.answers.mapIndexed { answerIndex, answer ->
        if (answerIndex == index) answer.copy(state = newAnswerState) else answer
    }
    return this.copy(
        answers = newAnswers,
        isAnswered = if (newAnswerState == AnswerState.Correct) true else this.isAnswered
    )
}
