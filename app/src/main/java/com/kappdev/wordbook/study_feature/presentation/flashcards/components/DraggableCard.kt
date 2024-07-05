package com.kappdev.wordbook.study_feature.presentation.flashcards.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kappdev.wordbook.study_feature.presentation.flashcards.CardDragDirection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.abs

fun Modifier.draggableCard(
    dragProgress: (progress: Float) -> Unit,
    swipeCard: Flow<CardDragDirection>,
    enabled: Boolean = true,
    dragThreshold: Dp = 140.dp,
    dragRotationDegree: Float = 15f,
    onDragComplete: (CardDragDirection) -> Unit
) = composed {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val dragThresholdPx = with(density) { dragThreshold.toPx() }

    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    LaunchedEffect(offsetX) {
        val progress = (offsetX / dragThresholdPx).coerceIn(-1f, 1f)
        dragProgress(progress)
    }

    fun snapToStart() {
        dragProgress(0f)
        offsetX = 0f
        offsetY = 0f
    }

    fun swipeAway(direction: CardDragDirection) = scope.launch {
        val endPoint = (screenWidthPx * 2)
        val endpointX = if (direction == CardDragDirection.Right) endPoint else -endPoint

        val swipeSpec = tween<Float>(durationMillis = 400, easing = LinearEasing)
        animate(offsetX, endpointX, animationSpec = swipeSpec) { value, _ -> offsetX = value }

        onDragComplete(direction)
        snapToStart()
    }

    fun backToStart() {
        val spec = tween<Float>(durationMillis = 300, easing = LinearOutSlowInEasing)
        scope.launch {
            animate(offsetX, 0f, animationSpec = spec) { value, _ -> offsetX = value }
        }
        scope.launch {
            animate(offsetY, 0f, animationSpec = spec) { value, _ -> offsetY = value }
        }
    }

    LaunchedEffect(Unit) {
        swipeCard.collectLatest { direction ->
            swipeAway(direction)
        }
    }

    this
        .graphicsLayer {
            translationX = offsetX
            translationY = offsetY
            rotationZ = (offsetX / dragThresholdPx).coerceIn(-1f, 1f) * dragRotationDegree * -1
        }
        .pointerInput(enabled) {
            detectDragGestures(
                onDrag = { change, dragAmount ->
                    if (enabled) {
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                },
                onDragEnd = {
                    if (abs(offsetX) >= dragThresholdPx) {
                        when {
                            offsetX > 0 -> swipeAway(CardDragDirection.Right)
                            offsetX < 0 -> swipeAway(CardDragDirection.Left)
                        }
                    } else {
                        backToStart()
                    }
                }
            )
        }
}