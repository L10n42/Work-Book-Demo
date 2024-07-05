package com.kappdev.wordbook.study_feature.presentation.writing.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.kappdev.wordbook.R
import com.kappdev.wordbook.core.presentation.common.FABPadding
import com.kappdev.wordbook.core.presentation.common.InputField
import com.kappdev.wordbook.core.presentation.common.VerticalSpace
import com.kappdev.wordbook.study_feature.presentation.common.AnswerState
import com.kappdev.wordbook.study_feature.presentation.common.StudyState
import com.kappdev.wordbook.study_feature.presentation.common.components.CongratulationView
import com.kappdev.wordbook.study_feature.presentation.common.components.StudyAnimatedContent
import com.kappdev.wordbook.study_feature.presentation.common.components.StudyProgressTopBar
import com.kappdev.wordbook.study_feature.presentation.writing.WritingState
import com.kappdev.wordbook.study_feature.presentation.writing.WritingViewModel

@Composable
fun WritingScreen(
    navController: NavHostController,
    collectionId: Int?,
    viewModel: WritingViewModel = hiltViewModel()
) {
    val screenScrollState = rememberScrollState()
    val inStudyingState = viewModel.studyState == StudyState.Studying

    LaunchedEffect(Unit) {
        if (collectionId != null && collectionId > 0) {
            viewModel.getDataIfNeed(collectionId)
        } else {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            StudyProgressTopBar(
                title = stringResource(R.string.writing),
                learned = viewModel.learnedCards.size,
                total = viewModel.totalCardsSize,
                isProgressVisible = inStudyingState,
                isElevated = screenScrollState.canScrollBackward,
                onBack = { navController.popBackStack() }
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            val answerState = viewModel.writingState?.answerState
            AnimatedWritingButtons(
                isVisible = inStudyingState && answerState != null,
                answerState = answerState,
                onShowAnswer = { viewModel.showAnswer() },
                onSubmit = { viewModel.submitAnswer() },
                onNext = { viewModel.nextCard() }
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { pv ->
        Crossfade(
            targetState = viewModel.studyState,
            modifier = Modifier
                .fillMaxSize()
                .padding(pv),
            label = "Writing content transition"
        ) { targetStudyState ->
            when (targetStudyState) {
                StudyState.Finished -> {
                    CongratulationView(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(screenScrollState),
                        onStudyAgain = { viewModel.resetWriting() },
                        onLeave = { navController.popBackStack() }
                    )
                }

                StudyState.Studying -> {
                    WritingContent(
                        viewModel = viewModel,
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(screenScrollState)
                            .padding(bottom = FABPadding)
                    )
                }

                StudyState.Loading -> WritingLoading(Modifier.fillMaxSize())

                else -> {}
            }
        }
    }
}

@Composable
private fun WritingContent(
    viewModel: WritingViewModel,
    modifier: Modifier = Modifier
) {
    StudyAnimatedContent(
        targetState = viewModel.writingState,
        contentKey = { it?.id }
    ) { targetState ->
        if (targetState != null) {
            WritingStateContent(targetState, modifier) {
                viewModel.updateAnswer(it)
            }
        }
    }
}

@Composable
private fun WritingStateContent(
    state: WritingState,
    modifier: Modifier,
    updateAnswer: (newAnswer: String) -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        VerticalSpace(32.dp)

        WritingCard(
            state = state,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        VerticalSpace(64.dp)

        InputField(
            value = state.answer,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            readOnly = (state.answerState != AnswerState.Unchecked),
            label = stringResource(R.string.answer),
            hint = stringResource(R.string.enter_answer),
            onValueChange = updateAnswer
        )
    }
}