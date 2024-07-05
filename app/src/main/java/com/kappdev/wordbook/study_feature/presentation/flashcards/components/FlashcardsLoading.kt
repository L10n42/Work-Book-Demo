package com.kappdev.wordbook.study_feature.presentation.flashcards.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.kappdev.wordbook.core.presentation.common.ShimmerLoading

@Composable
fun FlashcardsLoading(
    modifier: Modifier
) {
    val transition = rememberInfiniteTransition(label = "Shuffle transition")

    val cardRotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 500,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "Cars shuffle loading animation",
    )

    ShimmerLoading { shimmerBrush ->
        Box(modifier) {
            LoadingFlashcard(
                shimmerBrush,
                Modifier.rotate(-4f + cardRotation)
            )
            LoadingFlashcard(
                shimmerBrush,
                Modifier.rotate(4f - cardRotation)
            )
            LoadingFlashcard(shimmerBrush)
        }
    }
}

@Composable
private fun BoxScope.LoadingFlashcard(
    brush: Brush,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp)
) {
    Box(
        modifier
            .matchParentSize()
            .shadow(
                elevation = 8.dp,
                shape = shape,
                spotColor = MaterialTheme.colorScheme.primary,
                ambientColor = MaterialTheme.colorScheme.primary
            )
            .background(MaterialTheme.colorScheme.surfaceVariant, shape)
            .background(brush, shape)
    )
}