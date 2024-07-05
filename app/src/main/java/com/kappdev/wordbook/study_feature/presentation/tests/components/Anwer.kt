package com.kappdev.wordbook.study_feature.presentation.tests.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kappdev.wordbook.R
import com.kappdev.wordbook.core.domain.util.vibrateFor
import com.kappdev.wordbook.settings_feature.domain.LocalAppSettings
import com.kappdev.wordbook.settings_feature.domain.isAllowed
import com.kappdev.wordbook.study_feature.presentation.common.AnswerState
import com.kappdev.wordbook.theme.CrimsonEmber
import com.kappdev.wordbook.theme.SpringGreen
import kotlinx.coroutines.delay

@Composable
fun Answer(
    text: String,
    state: AnswerState,
    modifier: Modifier = Modifier,
    onCheck: () -> Unit
) {
    val appSettings = LocalAppSettings.current
    val context = LocalContext.current

    var oldState by remember { mutableStateOf(state) }

    SideEffect {
        if (oldState != state && state == AnswerState.Wrong && appSettings.vibration.isAllowed()) {
            context.vibrateFor(300)
        }
        oldState = state
    }

    val rotation by animateFloatAsState(
        targetValue = when (state) {
            AnswerState.Correct, AnswerState.Wrong -> 180f
            AnswerState.Unchecked -> 0f
        },
        animationSpec = tween(400, easing = LinearOutSlowInEasing),
        label = "Answer rotation"
    )

    Box(
        modifier
            .graphicsLayer {
                rotationX = rotation
                cameraDistance = 10f * density
            }
    ) {
        if (rotation <= 90f) {
            AnswerView(
                text = text,
                onCheck = onCheck,
                state = AnswerState.Unchecked,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            AnswerView(
                text = text,
                onCheck = onCheck,
                state = state,
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer { rotationX = 180f }
            )
        }
    }
}

@Composable
private fun AnswerView(
    text: String,
    state: AnswerState,
    modifier: Modifier,
    onCheck: () -> Unit
) {
    var animateIcon by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(200)
        animateIcon = true
    }

    val backgroundColor = when (state) {
        AnswerState.Correct -> SpringGreen.copy(0.5f)
        AnswerState.Wrong -> CrimsonEmber.copy(0.5f)
        AnswerState.Unchecked -> MaterialTheme.colorScheme.background
    }

    val borderColor = when (state) {
        AnswerState.Correct -> SpringGreen
        AnswerState.Wrong -> CrimsonEmber
        AnswerState.Unchecked -> MaterialTheme.colorScheme.onSurface.copy(0.25f)
    }

    Row(
        modifier = modifier
            .height(54.dp)
            .background(backgroundColor, AnswerShape)
            .border(1.dp, borderColor, AnswerShape)
            .clip(AnswerShape)
            .clickable(enabled = (state == AnswerState.Unchecked), onClick = onCheck)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = text,
            maxLines = 1,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        when (state) {
            AnswerState.Unchecked -> {}
            AnswerState.Correct -> AnimatedAnswerIcon(R.drawable.avd_done, atEnd = animateIcon)
            AnswerState.Wrong -> AnimatedAnswerIcon(R.drawable.avd_close, atEnd = animateIcon)
        }
    }
}

@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
private fun AnimatedAnswerIcon(
    @DrawableRes iconRes: Int,
    atEnd: Boolean,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        val avdIcon = AnimatedImageVector.animatedVectorResource(iconRes)
        Icon(
            painter = rememberAnimatedVectorPainter(
                animatedImageVector = avdIcon,
                atEnd = atEnd
            ),
            modifier = modifier,
            tint = MaterialTheme.colorScheme.onSurface,
            contentDescription = null
        )
    }
}

private val AnswerShape = RoundedCornerShape(8.dp)