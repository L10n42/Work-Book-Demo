package com.kappdev.wordbook.core.presentation.common

import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kappdev.wordbook.R

@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
fun AnimatedOptionsButton(
    modifier: Modifier = Modifier,
    optionsOpened: Boolean,
    onClick: () -> Unit
) {
    val avdIcon = AnimatedImageVector.animatedVectorResource(R.drawable.avd_tune)
    androidx.compose.material3.IconButton(
        modifier = modifier,
        onClick = onClick
    ) {
        Icon(
            painter = rememberAnimatedVectorPainter(
                animatedImageVector = avdIcon,
                atEnd = optionsOpened
            ),
            tint = MaterialTheme.colorScheme.onSurface,
            contentDescription = "Options"
        )
    }
}