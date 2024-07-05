package com.kappdev.wordbook.study_feature.presentation.flashcards.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kappdev.wordbook.R
import com.kappdev.wordbook.core.domain.util.vibrateFor
import com.kappdev.wordbook.settings_feature.domain.LocalAppSettings
import com.kappdev.wordbook.settings_feature.domain.Vibration
import com.kappdev.wordbook.study_feature.presentation.flashcards.CardDragDirection
import com.kappdev.wordbook.theme.ButtercreamYellow
import com.kappdev.wordbook.theme.SpringGreen
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun Flashcard(
    swipeCard: Flow<CardDragDirection>,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    reversedReview: Boolean = false,
    shape: Shape = RoundedCornerShape(16.dp),
    onSwipe: (direction: CardDragDirection) -> Unit,
    backSide: @Composable BoxScope.() -> Unit,
    frontSide: @Composable BoxScope.() -> Unit
) {
    val context = LocalContext.current
    val appSettings = LocalAppSettings.current
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    val defaultCardSide = if (reversedReview) CardSide.Back else CardSide.Front
    var cardSide by rememberSaveable { mutableStateOf(defaultCardSide) }

    val scope = rememberCoroutineScope()
    val cardRotation = remember { Animatable(cardSide.angle) }

    fun flipCard() = scope.launch {
        cardRotation.animateTo(cardSide.flipped.angle, tween(400, easing = FastOutSlowInEasing))
        cardSide = cardSide.flipped
    }

    fun snapCard() = scope.launch {
        cardRotation.snapTo(defaultCardSide.angle)
        cardSide = defaultCardSide
    }

    var dragProgress by remember { mutableStateOf(0f) }
    val thresholdCrossed by remember { derivedStateOf { abs(dragProgress) == 1f } }

    LaunchedEffect(thresholdCrossed) {
        if (thresholdCrossed && appSettings.vibration == Vibration.Allowed) {
            context.vibrateFor(100)
        }
    }

    val indicatorColor = if (dragProgress < 0 && isRtl || dragProgress > 0 && !isRtl) SpringGreen else ButtercreamYellow
    val progressColor = indicatorColor.copy(abs(dragProgress))

    Box(
        modifier
            .draggableCard(
                onDragComplete = { direction ->
                    snapCard()
                    onSwipe(direction)
                },
                enabled = enabled,
                dragProgress = { dragProgress = it },
                swipeCard = swipeCard
            )
            .graphicsLayer {
                rotationY = cardRotation.value
                cameraDistance = 20f * density
            }
            .shadow(
                elevation = 8.dp,
                shape = shape,
                spotColor = MaterialTheme.colorScheme.primary,
                ambientColor = MaterialTheme.colorScheme.primary
            )
            .background(MaterialTheme.colorScheme.surfaceVariant, shape)
            .border(width = 2.dp, progressColor, shape)
            .clip(shape)
            .clickable(enabled) { flipCard() }
    ) {
        if (cardRotation.value <= 90f) {
            frontSide()
        } else {
            ReflectHorizontally {
                backSide()
            }
        }

        if (dragProgress != 0f) {
            val titleRes = if (dragProgress < 0 && isRtl || dragProgress > 0 && !isRtl) R.string.learned else R.string.relearn
            DragIndicator(
                title = stringResource(titleRes),
                color = indicatorColor,
                alpha = abs(dragProgress),
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .graphicsLayer {
                        rotationY = if (cardSide == CardSide.Front) 0f else 180f
                    }
            )
        }
    }
}

@Composable
private fun DragIndicator(
    modifier: Modifier = Modifier,
    title: String,
    alpha: Float,
    color: Color,
) {
    Box(
        modifier = modifier
            .height(56.dp)
            .alpha(alpha)
            .background(color)
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            color = Color.Black,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun BoxScope.ReflectHorizontally(
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .matchParentSize()
            .graphicsLayer { rotationY = 180f },
        content = content
    )
}

private enum class CardSide(val angle: Float) {
    Front(0f) {
        override val flipped: CardSide
            get() = Back
    },
    Back(180f) {
        override val flipped: CardSide
            get() = Front
    };

    abstract val flipped: CardSide
}