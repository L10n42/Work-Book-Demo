package com.kappdev.wordbook.study_feature.presentation.tests.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kappdev.wordbook.R

@Composable
fun AnimatedNextButton(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + slideInVertically { it },
        exit = slideOutVertically { it } + fadeOut(),
        modifier = modifier
    ) {
        NextButton(
            modifier = Modifier.padding(8.dp),
            onClick = onClick
        )
    }
}

@Composable
fun NextButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val buttonScale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        label = "ButtonOnPressScale"
    )

    Button(
        modifier = modifier
            .scale(buttonScale)
            .size(180.dp, 50.dp),
        onClick = onClick,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(16, 50, 50, 16),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.background,
            disabledContentColor = MaterialTheme.colorScheme.onBackground
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 0.dp,
            focusedElevation = 4.dp,
            hoveredElevation = 4.dp,
            disabledElevation = 0.dp,
        )
    ) {
        Text(
            text = stringResource(R.string.next),
            maxLines = 1,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}