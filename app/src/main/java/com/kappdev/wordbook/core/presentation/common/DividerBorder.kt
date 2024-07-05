package com.kappdev.wordbook.core.presentation.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.dividerBorder(
    isVisible: Boolean = true,
    width: Dp = 1.dp
) = composed {
    val dividerColor = MaterialTheme.colorScheme.onSurface.copy(0.25f)
    this.drawWithContent {
        drawContent()
        if (isVisible) {
            drawLine(
                color = dividerColor,
                start = Offset(0f, size.height),
                end = Offset(size.width, size.height),
                strokeWidth = width.toPx()
            )
        }
    }
}