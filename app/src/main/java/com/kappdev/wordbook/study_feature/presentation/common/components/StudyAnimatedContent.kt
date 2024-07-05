package com.kappdev.wordbook.study_feature.presentation.common.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun <S> StudyAnimatedContent(
    targetState: S,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center,
    label: String = "StudyAnimatedContent",
    contentKey: (targetState: S) -> Any? = { it },
    content: @Composable AnimatedContentScope.(targetState: S) -> Unit
) = AnimatedContent(
    targetState = targetState,
    modifier = modifier,
    contentAlignment = contentAlignment,
    label = label,
    transitionSpec = { studyContentTransition() },
    contentKey = contentKey,
    content = content
)

private fun <S> AnimatedContentTransitionScope<S>.studyContentTransition(): ContentTransform {
    return slideIntoContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Start,
        animationSpec = tween(TRANSITION_DURATION)
    ) togetherWith
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(TRANSITION_DURATION)
            )
}

private const val TRANSITION_DURATION = 500