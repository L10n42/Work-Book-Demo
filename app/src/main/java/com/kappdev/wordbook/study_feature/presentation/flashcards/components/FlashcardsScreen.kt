package com.kappdev.wordbook.study_feature.presentation.flashcards.components

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.kappdev.wordbook.R
import com.kappdev.wordbook.core.presentation.common.VerticalSpace
import com.kappdev.wordbook.study_feature.presentation.common.StudyState
import com.kappdev.wordbook.study_feature.presentation.common.components.CongratulationView
import com.kappdev.wordbook.study_feature.presentation.common.components.StudyProgress
import com.kappdev.wordbook.study_feature.presentation.common.components.StudyTopBar
import com.kappdev.wordbook.study_feature.presentation.flashcards.CardDragDirection
import com.kappdev.wordbook.study_feature.presentation.flashcards.FlashcardsViewModel

@Composable
fun FlashcardsScreen(
    navController: NavHostController,
    collectionId: Int?,
    viewModel: FlashcardsViewModel = hiltViewModel()
) {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val screenScrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        if (collectionId != null && collectionId > 0) {
            viewModel.getDataIfNeed(collectionId)
        } else {
            navController.popBackStack()
        }
    }

    BackHandler {
        if (viewModel.studyState == StudyState.Studying) {
            viewModel.popLastCard()
        } else {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            StudyTopBar(
                title = stringResource(R.string.flashcards),
                isElevated = screenScrollState.canScrollBackward,
                onBack = { navController.popBackStack() }
            )
        },
        floatingActionButton = {
            AnimatedFlashcardButtons(
                isVisible = viewModel.studyState == StudyState.Studying,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp),
                onRelearn = {
                    when {
                        isRtl -> viewModel.swipeCardWithAnimation(CardDragDirection.Right)
                        else -> viewModel.swipeCardWithAnimation(CardDragDirection.Left)
                    }
                },
                onLearned = {
                    when {
                        isRtl -> viewModel.swipeCardWithAnimation(CardDragDirection.Left)
                        else -> viewModel.swipeCardWithAnimation(CardDragDirection.Right)
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { pv ->
        Crossfade(
            targetState = viewModel.studyState,
            modifier = Modifier
                .fillMaxSize()
                .padding(pv),
            label = "Flashcards content transition"
        ) { targetState ->
            when (targetState) {
                StudyState.Finished -> {
                    CongratulationView(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(screenScrollState),
                        onStudyAgain = { viewModel.resetFlashcards() },
                        onLeave = { navController.popBackStack() }
                    )
                }
                StudyState.Studying -> {
                    FlashCardsContent(
                        viewModel = viewModel,
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(screenScrollState)
                    )
                }
                StudyState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        FlashcardsLoading(modifier = Modifier.adaptiveCardSize())
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun FlashCardsContent(
    viewModel: FlashcardsViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        VerticalSpace(4.dp)
        StudyProgress(
            learned = viewModel.learnedCards.size,
            total = viewModel.totalCardsSize,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
        CardsPile(
            cards = viewModel.unlearnedCards,
            swipeCard = viewModel.swipeCardFlow,
            modifier = Modifier
                .padding(vertical = 42.dp)
                .adaptiveCardSize(),
            onRelearn = { viewModel.relearn() },
            onLearned = { viewModel.learn() },
            onDefinitionSpeak = { viewModel.speakDefinition(it) },
            onTermSpeak = { viewModel.speakTerm(it) }
        )
    }
}

private fun Modifier.adaptiveCardSize() = composed {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val heightRatio = if (isLandscape) 1f else 1.7f
    val widthFraction = if (isLandscape) 0.7f else 0.82f

    this
        .fillMaxWidth(widthFraction)
        .aspectRatio(1f / heightRatio)
}