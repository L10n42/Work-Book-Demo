package com.kappdev.wordbook.main_feature.presentation.common.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color

@Composable
fun ContentLoading(
    content: @Composable (Color) -> Unit
) {
    val transition = rememberInfiniteTransition(label = "ContentLoadingTransition")
    val colorAlpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.72f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ContentLoadingColorAlpha"
    )
    content(MaterialTheme.colorScheme.onBackground.copy(colorAlpha))
}