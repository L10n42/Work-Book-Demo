package com.kappdev.wordbook.core.presentation.common

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

@Composable
fun AnimateDottedText(
    text: String,
    modifier: Modifier = Modifier,
    preoccupySpace: Boolean = true,
    style: TextStyle = LocalTextStyle.current.copy(
        color = MaterialTheme.colorScheme.onSurface,
        fontSize = 16.sp,
        lineHeight = 18.sp
    ),
    cycleDuration: Int = 1000 // Milliseconds
) {
    val transition = rememberInfiniteTransition(label = "Dots Transition")

    val visibleDotsCount = transition.animateValue(
        initialValue = 0,
        targetValue = 4,
        typeConverter = Int.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = cycleDuration,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "Visible Dots Count"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (preoccupySpace) {
            Text(
                text = text + ".".repeat(3),
                modifier = Modifier.alpha(0f),
                style = style
            )
        }
        Text(
            text = text + ".".repeat(visibleDotsCount.value),
            style = style
        )
    }
}