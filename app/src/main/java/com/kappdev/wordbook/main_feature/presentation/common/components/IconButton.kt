package com.kappdev.wordbook.main_feature.presentation.common.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.kappdev.wordbook.core.presentation.common.AutoMirroredIcon

@Composable
fun IconButtonWithoutIndication(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Icon(
        imageVector = icon,
        tint = MaterialTheme.colorScheme.onSurface,
        contentDescription = null,
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            enabled = enabled,
            indication = null,
            onClick = onClick
        )
    )
}

@Composable
fun IconButton(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    androidx.compose.material3.IconButton(onClick, modifier, enabled) {
        AutoMirroredIcon(
            imageVector = icon,
            tint = MaterialTheme.colorScheme.onSurface,
            contentDescription = null
        )
    }
}