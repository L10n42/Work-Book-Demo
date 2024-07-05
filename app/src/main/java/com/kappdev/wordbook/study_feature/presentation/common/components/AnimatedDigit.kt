package com.kappdev.wordbook.study_feature.presentation.common.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle

@Composable
fun AnimatedDigit(
    count: Int,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyMedium
) {
    var oldCount by remember { mutableStateOf(count) }

    SideEffect {
        oldCount = count
    }

    Row(modifier) {
        val countString = count.toString()
        val oldCountString = oldCount.toString()
        for (i in countString.indices) {
            val oldChar = oldCountString.getOrNull(i)
            val newChar = countString[i]
            val char = if (oldChar == newChar) oldCountString[i] else countString[i]

            AnimatedContent(
                targetState = char,
                transitionSpec = {
                    fadeIn() + slideInVertically {
                        if (oldChar != null) {
                            if (oldChar.digitToInt() > newChar.digitToInt()) -it else it
                        } else it
                    } togetherWith slideOutVertically {
                        if (oldChar != null) {
                            if (oldChar.digitToInt() > newChar.digitToInt()) it else -it
                        } else -it
                    } + fadeOut()
                },
                label = "DigitTransition"
            ) { animatedChar ->
                Text(
                    text = animatedChar.toString(),
                    style = style,
                    softWrap = false
                )
            }
        }
    }
}