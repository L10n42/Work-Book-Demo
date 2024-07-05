package com.kappdev.wordbook.study_feature.presentation.common.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kappdev.wordbook.settings_feature.domain.LocalAppSettings
import com.kappdev.wordbook.settings_feature.domain.ProgressBarStyle

@Composable
fun StudyProgress(
    learned: Int,
    total: Int,
    modifier: Modifier
) {
    val settings = LocalAppSettings.current
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        CardsCountProgress(learned, total)
        val progress = calculatedProgress(learned, total)

        when (settings.progressBarStyle) {
            ProgressBarStyle.Simple -> SimpleProgressBar(progress, Modifier.fillMaxWidth())
            ProgressBarStyle.Advanced -> AnimatedProgressBar(progress, Modifier.fillMaxWidth())
        }
    }
}

private fun calculatedProgress(learned: Int, total: Int): Float {
    if (learned < 0 || total <= 0) return 0f
    return learned.toFloat() / total.toFloat()
}

@Composable
private fun CardsCountProgress(
    learned: Int,
    total: Int,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current.copy(
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onSurface,
    )
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        AnimatedDigit(count = learned)
        Text(text = "/", style = style)
        AnimatedDigit(count = total)
    }
}