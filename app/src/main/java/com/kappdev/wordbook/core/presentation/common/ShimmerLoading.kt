package com.kappdev.wordbook.core.presentation.common

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun ShimmerLoading(
    color: Color = MaterialTheme.colorScheme.primary,
    content: @Composable (shimmerBrush: Brush) -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidthPx = with(LocalDensity.current) { configuration.screenWidthDp.dp.toPx() }

    val shimmerColors = listOf(
        color.copy(alpha = 0f),
        color.copy(alpha = 0.05f),
        color.copy(alpha = 0.1f),
        color.copy(alpha = 0.2f),
        color.copy(alpha = 0.3f),
        color.copy(alpha = 0.2f),
        color.copy(alpha = 0.1f),
        color.copy(alpha = 0.05f),
        color.copy(alpha = 0f),
    )

    val transition = rememberInfiniteTransition(label = "Shimmer transition")

    val translateAnimation = transition.animateFloat(
        initialValue = 0f,
        targetValue = screenWidthPx + 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (screenWidthPx * 1.3f).roundToInt(),
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        label = "Shimmer loading animation",
    )

    content(
        Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(x = translateAnimation.value - 500f, y = 0.0f),
            end = Offset(x = translateAnimation.value, y = 270f)
        )
    )
}