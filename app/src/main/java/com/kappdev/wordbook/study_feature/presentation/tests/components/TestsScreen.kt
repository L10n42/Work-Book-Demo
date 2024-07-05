package com.kappdev.wordbook.study_feature.presentation.tests.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
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
import com.kappdev.wordbook.study_feature.presentation.common.StudyState
import com.kappdev.wordbook.study_feature.presentation.common.components.CongratulationView
import com.kappdev.wordbook.study_feature.presentation.common.components.StudyAnimatedContent
import com.kappdev.wordbook.study_feature.presentation.common.components.StudyProgressTopBar
import com.kappdev.wordbook.study_feature.presentation.tests.Answer
import com.kappdev.wordbook.study_feature.presentation.tests.TestState
import com.kappdev.wordbook.study_feature.presentation.tests.TestsViewModel

@Composable
fun TestsScreen(
    navController: NavHostController,
    collectionId: Int?,
    viewModel: TestsViewModel = hiltViewModel()
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
                title = stringResource(R.string.tests),
                learned = viewModel.learnedCards.size,
                total = viewModel.totalCardsSize,
                isProgressVisible = inStudyingState,
                isElevated = screenScrollState.canScrollBackward,
                onBack = { navController.popBackStack() }
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            val testAnsweredCorrectly = viewModel.testState?.isAnswered == true
            AnimatedNextButton(isVisible = testAnsweredCorrectly && inStudyingState) {
                viewModel.nextTest()
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { pv ->
        Crossfade(
            targetState = viewModel.studyState,
            modifier = Modifier
                .fillMaxSize()
                .padding(pv),
            label = "Tests content transition"
        ) { targetStudyState ->
            when (targetStudyState) {
                StudyState.Finished -> {
                    CongratulationView(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(screenScrollState),
                        onStudyAgain = { viewModel.resetTests() },
                        onLeave = { navController.popBackStack() }
                    )
                }
                StudyState.Studying -> {
                    TestsContent(
                        viewModel = viewModel,
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(screenScrollState)
                            .padding(bottom = FABPadding)
                    )
                }
                StudyState.Loading -> {
                    TestLoading(Modifier.fillMaxSize())
                }
                else -> {}
            }
        }
    }
}

@Composable
fun TestsContent(
    modifier: Modifier = Modifier,
    viewModel: TestsViewModel
) {
    StudyAnimatedContent(
        targetState = viewModel.testState,
        contentKey = { it?.id }
    ) { targetState ->
        if (targetState != null) {
            TestContent(targetState, modifier) {
                viewModel.answer(it)
            }
        }
    }
}

@Composable
private fun TestContent(
    testState: TestState,
    modifier: Modifier = Modifier,
    onCheck: (index: Int) -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        QuestionCard(
            question = testState.question,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 32.dp)
        )

        Answers(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            answers = testState.answers,
            onCheck = onCheck
        )
    }
}

@Composable
private fun Answers(
    answers: List<Answer>,
    modifier: Modifier = Modifier,
    onCheck: (index: Int) -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Bottom)
    ) {
        answers.forEachIndexed { index, answer ->
            Answer(
                text = answer.text,
                state = answer.state,
                modifier = Modifier.fillMaxWidth(),
                onCheck = { onCheck(index) }
            )
        }
    }
}