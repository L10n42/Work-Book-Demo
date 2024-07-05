package com.kappdev.wordbook.study_feature.presentation.common.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kappdev.wordbook.core.presentation.common.dividerBorder
import com.kappdev.wordbook.main_feature.presentation.common.components.IconButton

@Composable
fun StudyTopBar(
    title: String,
    isElevated: Boolean = false,
    onBack: () -> Unit
) {
    val animElevation by animateDpAsState(
        targetValue = if (isElevated) 6.dp else 0.dp,
        label = "TopAppBarElevation"
    )

    Box(
        Modifier
            .fillMaxWidth()
            .shadow(elevation = animElevation)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 4.dp)
            .dividerBorder(isElevated)
    ) {
        IconButton(
            onClick = onBack,
            icon = Icons.Rounded.Close,
            modifier = Modifier.align(Alignment.CenterStart)
        )
        Text(
            text = title,
            maxLines = 1,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 50.dp)
        )
    }
}

@Composable
fun StudyProgressTopBar(
    title: String,
    learned: Int,
    total: Int,
    isProgressVisible: Boolean = true,
    isElevated: Boolean = false,
    onBack: () -> Unit
) {
    val animElevation by animateDpAsState(
        targetValue = if (isElevated) 6.dp else 0.dp,
        label = "TopAppBarElevation"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = animElevation)
            .background(MaterialTheme.colorScheme.surface)
            .dividerBorder(isElevated)
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 4.dp)
        ) {
            IconButton(
                onClick = onBack,
                icon = Icons.Rounded.Close,
                modifier = Modifier.align(Alignment.CenterStart)
            )
            Text(
                text = title,
                maxLines = 1,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 50.dp)
            )
        }
        AnimatedVisibility(
            visible = isProgressVisible,
            enter = fadeIn() + slideInVertically(),
            exit = slideOutVertically() + fadeOut(),
        ) {
            StudyProgress(
                learned = learned,
                total = total,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }
    }
}