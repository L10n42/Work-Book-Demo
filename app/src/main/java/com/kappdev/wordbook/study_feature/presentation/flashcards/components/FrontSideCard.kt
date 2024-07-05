package com.kappdev.wordbook.study_feature.presentation.flashcards.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.kappdev.wordbook.core.presentation.common.Speaker

@Composable
fun FrontSizeCard(
    term: String,
    transcription: String,
    modifier: Modifier = Modifier,
    onSpeak: (String) -> Unit
) {
    Box(
        modifier = modifier
    ) {
        Text(
            text = term,
            maxLines = 5,
            fontSize = 16.sp,
            lineHeight = 18.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center),
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium,
            overflow = TextOverflow.Ellipsis
        )

        Speaker(
            textToSpeak = term,
            textToShow = transcription,
            speak = onSpeak,
            modifier = Modifier.align(Alignment.TopEnd)
        )
    }
}