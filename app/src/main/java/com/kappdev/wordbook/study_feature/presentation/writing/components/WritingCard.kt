package com.kappdev.wordbook.study_feature.presentation.writing.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kappdev.wordbook.R
import com.kappdev.wordbook.study_feature.presentation.common.AnswerState
import com.kappdev.wordbook.study_feature.presentation.writing.WritingState

@Composable
fun WritingCard(
    state: WritingState,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp)
) {
    Column(
        modifier
            .shadow(4.dp, shape)
            .background(MaterialTheme.colorScheme.surfaceVariant, shape)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = state.question,
            maxLines = 5,
            fontSize = 16.sp,
            lineHeight = 18.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
            overflow = TextOverflow.Ellipsis
        )

        AnimatedAnswer(
            isVisible = state.showAnswer || state.answerState != AnswerState.Unchecked,
            answer = state.correctAnswer
        )
    }
}

@Composable
private fun AnimatedAnswer(
    isVisible: Boolean,
    answer: String,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        modifier = modifier,
        enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
        exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut(),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnswerDivider(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 6.dp))
            Text(
                text = answer,
                maxLines = 1,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun AnswerDivider(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Divider(Modifier.weight(1f))
        Text(
            text = stringResource(R.string.correct_answer),
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Divider(Modifier.weight(1f))
    }
}

@Composable
private fun Divider(
    modifier: Modifier = Modifier
) {
    Box(
        modifier
            .height(0.72.dp)
            .background(MaterialTheme.colorScheme.onSurface.copy(0.25f), CircleShape)
    )
}
