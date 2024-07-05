package com.kappdev.wordbook.core.presentation.common

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kappdev.wordbook.main_feature.presentation.common.components.IconButton

@Composable
fun SimpleTopAppBar(
    title: String,
    isElevated: Boolean = true,
    onBack: () -> Unit
) {
    val animElevation by animateDpAsState(
        targetValue = if (isElevated) 6.dp else 0.dp,
        label = "TopAppBar Elevation"
    )

    TopAppBar(
        elevation = animElevation,
        modifier = Modifier.dividerBorder(isElevated),
        backgroundColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(
                text = title,
                maxLines = 1,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        navigationIcon = {
            IconButton(
                icon = Icons.Rounded.ArrowBack,
                onClick = onBack
            )
        }
    )
}