package com.kappdev.wordbook.core.presentation.common

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CountdownSnackbar(
    snackbarData: SnackbarData,
    modifier: Modifier = Modifier,
    timerDuration: Int = 5_000
) {
    var timerDigit by remember { mutableIntStateOf(timerDuration / 1000) }
    val timerProgress = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        val progressAnimation = launch {
            timerProgress.animateTo(0f, tween(timerDuration, easing = LinearEasing))
        }
        val timerAnimation = launch {
            while (timerDigit > 0) {
                delay(1000)
                timerDigit--
            }
        }
        progressAnimation.join()
        timerAnimation.join()
        snackbarData.performAction()
    }

    Snackbar(
        modifier = modifier,
        actionContentColor = MaterialTheme.colorScheme.primary,
        action = {
            Box(
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = timerProgress.value,
                    modifier = Modifier.size(30.dp),
                    strokeCap = StrokeCap.Round,
                    strokeWidth = 3.dp
                )
                Text(timerDigit.toString())
            }
        },
        content = {
            Text(snackbarData.visuals.message)
        }
    )
}