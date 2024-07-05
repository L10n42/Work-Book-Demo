package com.kappdev.wordbook.settings_feature.presentations.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kappdev.wordbook.core.presentation.common.CustomModalBottomSheet
import com.kappdev.wordbook.settings_feature.domain.Theme
import com.kappdev.wordbook.settings_feature.domain.getIcon
import com.kappdev.wordbook.settings_feature.domain.getStringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeChooserSheet(
    selected: Theme,
    onDismiss: () -> Unit,
    onThemeChange: (newTheme: Theme) -> Unit
) {
    CustomModalBottomSheet(
        onDismissRequest = onDismiss
    ) { _ ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Theme.entries.forEach { theme ->
                ThemeItem(
                    icon = theme.getIcon(),
                    selected = (theme == selected),
                    title = theme.getStringResource(),
                    onClick = {
                        onThemeChange(theme)
                    }
                )
            }
        }
    }
}

@Composable
private fun ThemeItem(
    icon: ImageVector,
    title: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
    onClick: () -> Unit
) {
    val interSource = remember { MutableInteractionSource() }
    val pressed by interSource.collectIsPressedAsState()

    val animShadow by animateDpAsState(
        targetValue = if (pressed || selected) 0.dp else 5.dp,
        label = "Shadow Animation"
    )

    val background = if (pressed || selected) {
        MaterialTheme.colorScheme.background
    } else {
        MaterialTheme.colorScheme.surface
    }

    Column(
        modifier = modifier
            .size(92.dp)
            .shadow(animShadow, shape)
            .background(background, shape)
            .clip(shape)
            .clickable(
                interactionSource = interSource,
                indication = null,
                onClick = onClick
            )
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxSize(0.64f),
            contentDescription = null
        )

        Text(
            text = title,
            maxLines = 1,
            fontSize = 14.sp,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}