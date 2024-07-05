package com.kappdev.wordbook.study_feature.presentation.tests

import com.kappdev.wordbook.study_feature.presentation.common.AnswerState

data class Answer(
    val id: Int,
    val text: String,
    val state: AnswerState
)
