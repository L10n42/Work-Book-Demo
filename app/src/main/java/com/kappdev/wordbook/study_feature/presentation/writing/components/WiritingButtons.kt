package com.kappdev.wordbook.study_feature.presentation.writing.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import com.kappdev.wordbook.R
import com.kappdev.wordbook.core.domain.util.vibrateFor
import com.kappdev.wordbook.settings_feature.domain.LocalAppSettings
import com.kappdev.wordbook.settings_feature.domain.isAllowed
import com.kappdev.wordbook.study_feature.presentation.common.AnswerState
import com.kappdev.wordbook.theme.ButtercreamYellow
import com.kappdev.wordbook.theme.CrimsonEmber
import com.kappdev.wordbook.theme.Graphite
import com.kappdev.wordbook.theme.SoftSkyBlue
import com.kappdev.wordbook.theme.SpringGreen

@Composable
fun AnimatedWritingButtons(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    answerState: AnswerState?,
    onShowAnswer: () -> Unit,
    onSubmit: () -> Unit,
    onNext: () -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + slideInVertically { it },
        exit = slideOutVertically { it } + fadeOut(),
        modifier = modifier
    ) {
        if (answerState != null) {
            WritingButtons(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                answerState = answerState,
                onShowAnswer = onShowAnswer,
                onSubmit = onSubmit,
                onNext = onNext
            )
        }
    }
}

@Composable
fun WritingButtons(
    modifier: Modifier = Modifier,
    answerState: AnswerState,
    onShowAnswer: () -> Unit,
    onSubmit: () -> Unit,
    onNext: () -> Unit
) {
    val context = LocalContext.current
    val appSettings = LocalAppSettings.current
    var oldState by remember { mutableStateOf(answerState) }

    SideEffect {
        if (oldState != answerState && answerState == AnswerState.Wrong && appSettings.vibration.isAllowed()) {
            context.vibrateFor(300)
        }
        oldState = answerState
    }

    val buttonsTextStyle = LocalTextStyle.current.copy(
        fontSize = 16.sp,
        color = Graphite,
        fontWeight = FontWeight.Medium
    )

    val textMeasurer = rememberTextMeasurer()
    val submitWidth = textMeasurer.measure(stringResource(R.string.submit), buttonsTextStyle).size.width
    val showAnswerWidth = textMeasurer.measure(stringResource(R.string.show_answer), buttonsTextStyle).size.width
    val nextWidth = textMeasurer.measure(stringResource(R.string.next), buttonsTextStyle).size.width
    val maxTextWidthDp = with(LocalDensity.current) { maxOf(submitWidth, showAnswerWidth, nextWidth).toDp() }
    val buttonWidth = max(BaseButtonWidth + maxTextWidthDp, MinButtonWidth)

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
    ) {
        ShowAnswerButton(
            modifier = Modifier.width(buttonWidth),
            textStyle = buttonsTextStyle,
            onClick = onShowAnswer
        )
        SubmitNextButton(
            modifier = Modifier.width(buttonWidth),
            textStyle = buttonsTextStyle,
            answerState = answerState,
            onClick = {
                when (answerState) {
                    AnswerState.Unchecked -> onSubmit()
                    AnswerState.Correct, AnswerState.Wrong -> onNext()
                }
            }
        )
    }
}

@Composable
private fun SubmitNextButton(
    modifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current,
    answerState: AnswerState,
    onClick: () -> Unit
) {
    WritingButton(
        title = when (answerState) {
            AnswerState.Unchecked -> stringResource(R.string.submit)
            AnswerState.Correct, AnswerState.Wrong -> stringResource(R.string.next)
        },
        color = when (answerState) {
            AnswerState.Wrong -> CrimsonEmber
            AnswerState.Correct -> SpringGreen
            AnswerState.Unchecked -> SoftSkyBlue
        },
        shape = RightShape,
        modifier = modifier,
        textStyle = textStyle,
        onClick = onClick
    )
}

@Composable
private fun ShowAnswerButton(
    modifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current,
    onClick: () -> Unit
) {
    WritingButton(
        title = stringResource(R.string.show_answer),
        color = ButtercreamYellow,
        shape = LeftShape,
        textStyle = textStyle,
        modifier = modifier,
        onClick = onClick
    )
}

@Composable
private fun WritingButton(
    title: String,
    color: Color,
    shape: Shape,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val buttonScale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        label = "ButtonOnPressScale"
    )

    val buttonElevation by animateDpAsState(
        targetValue = if (isPressed) 0.dp else 4.dp,
        label = "ButtonOnPressElevation"
    )

    val buttonColor by animateColorAsState(
        targetValue = color,
        label = "Button color animation"
    )

    Box(
        modifier = modifier
            .scale(buttonScale)
            .shadow(buttonElevation, shape)
            .background(buttonColor, shape)
            .clip(shape)
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = onClick
            )
            .height(50.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            maxLines = 1,
            style = textStyle,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private val BaseButtonWidth = 12.dp
private val MinButtonWidth = 140.dp

private val RightShape = RoundedCornerShape(16, 50, 50, 16)
private val LeftShape = RoundedCornerShape(50, 16, 16, 50)