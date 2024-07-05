package com.kappdev.wordbook.study_feature.presentation.flashcards.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ModelTraining
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.kappdev.wordbook.core.presentation.common.HorizontalSpace
import com.kappdev.wordbook.theme.ButtercreamYellow
import com.kappdev.wordbook.theme.Graphite
import com.kappdev.wordbook.theme.SpringGreen
import kotlin.math.max

@Composable
fun AnimatedFlashcardButtons(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    onRelearn: () -> Unit,
    onLearned: () -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + slideInVertically { it },
        exit = slideOutVertically { it } + fadeOut(),
        modifier = modifier
    ) {
        FlashcardButtons(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            onRelearn = onRelearn,
            onLearned = onLearned,
        )
    }
}

@Composable
fun FlashcardButtons(
    modifier: Modifier = Modifier,
    onRelearn: () -> Unit,
    onLearned: () -> Unit
) {
    val buttonsTextStyle = LocalTextStyle.current.copy(
        fontSize = 16.sp,
        color = Graphite,
        fontWeight = FontWeight.Medium
    )

    val textMeasurer = rememberTextMeasurer()
    val learnedWidth = textMeasurer.measure(stringResource(R.string.learned), buttonsTextStyle).size.width
    val relearnWidth = textMeasurer.measure(stringResource(R.string.relearn), buttonsTextStyle).size.width
    val maxTextWidthDp = with(LocalDensity.current) { max(learnedWidth, relearnWidth).toDp() }
    val buttonWidth = max(BaseButtonWidth + maxTextWidthDp, MinButtonWidth)

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        FlashcardButton(
            icon = Icons.Rounded.ModelTraining,
            title = stringResource(R.string.relearn),
            color = ButtercreamYellow,
            shape = RoundedCornerShape(topEnd = 16.dp, bottomStart = 16.dp),
            onClick = onRelearn,
            textStyle = buttonsTextStyle,
            modifier = Modifier.width(buttonWidth)
        )
        FlashcardButton(
            icon = Icons.Rounded.Verified,
            title = stringResource(R.string.learned),
            color = SpringGreen,
            shape = RoundedCornerShape(topStart = 16.dp, bottomEnd = 16.dp),
            onClick = onLearned,
            textStyle = buttonsTextStyle,
            modifier = Modifier.width(buttonWidth)
        )
    }
}

@Composable
private fun FlashcardButton(
    icon: ImageVector,
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
        targetValue = if (isPressed) 2.dp else 4.dp,
        label = "ButtonOnPressElevation"
    )

    val customModifier = modifier
        .scale(buttonScale)
        .shadow(buttonElevation, shape)
        .background(color, shape)
        .clickable(
            interactionSource = interactionSource,
            indication = LocalIndication.current,
            onClick = onClick
        )
        .padding(vertical = 14.dp)

    Row(
        modifier = customModifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Graphite
        )
        HorizontalSpace(8.dp)
        Text(
            text = title,
            maxLines = 1,
            style = textStyle,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private val BaseButtonWidth = 48.dp
private val MinButtonWidth = 130.dp