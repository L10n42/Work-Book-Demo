package com.kappdev.wordbook.core.presentation.common

import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

/** Displays the icon mirrored if the current layout direction is RTL */
@Composable
fun AutoMirroredIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    val layoutDirection = LocalLayoutDirection.current
    val isRtl = (layoutDirection == LayoutDirection.Rtl)
    val scaleX = if (isRtl) -1f else 1f
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = modifier.scale(scaleX, 1f),
        tint = tint
    )
}