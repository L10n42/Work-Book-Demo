package com.kappdev.wordbook.study_feature.presentation.common.components

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SimpleProgressBar(
    progress: Float,
    modifier: Modifier,
    strokeWidth: Dp = 4.dp,
    trackColor: Color = MaterialTheme.colorScheme.onSurface.copy(0.16f),
    strokeCap: StrokeCap = StrokeCap.Round
) {
    val progressAnim by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(720, easing = LinearOutSlowInEasing),
        label = "ProgressChangeAnimation"
    )
    LinearProgressIndicator(
        progress = progressAnim,
        modifier = modifier.height(strokeWidth),
        trackColor = trackColor,
        strokeCap = strokeCap,
    )
}