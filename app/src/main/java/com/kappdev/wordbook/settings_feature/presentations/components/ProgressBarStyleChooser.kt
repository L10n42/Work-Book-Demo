package com.kappdev.wordbook.settings_feature.presentations.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircleOutline
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kappdev.wordbook.R
import com.kappdev.wordbook.core.presentation.common.CustomModalBottomSheet
import com.kappdev.wordbook.settings_feature.domain.ProgressBarStyle
import com.kappdev.wordbook.study_feature.presentation.common.components.AnimatedProgressBar
import com.kappdev.wordbook.study_feature.presentation.common.components.SimpleProgressBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressBarStyleChooser(
    selectedStyle: ProgressBarStyle,
    onDismiss: () -> Unit,
    onStyleChanged: (ProgressBarStyle) -> Unit
) {
    CustomModalBottomSheet(
        onDismissRequest = onDismiss,
        skipPartiallyExpanded = true
    ) { _ ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            ProgressBarStyleItem(
                title = stringResource(R.string.simple),
                selected = (selectedStyle == ProgressBarStyle.Simple),
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onStyleChanged(ProgressBarStyle.Simple)
                }
            ) {
                SimpleProgressBarDemo(Modifier.fillMaxWidth())
            }

            ProgressBarStyleItem(
                title = stringResource(R.string.advanced),
                selected = (selectedStyle == ProgressBarStyle.Advanced),
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onStyleChanged(ProgressBarStyle.Advanced)
                }
            ) {
                AdvancedProgressBarDemo(Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun SimpleProgressBarDemo(
    modifier: Modifier
) {
    ProgressDemo { progress ->
        SimpleProgressBar(
            progress = progress,
            modifier = modifier
        )
    }
}

@Composable
private fun AdvancedProgressBarDemo(
    modifier: Modifier
) {
    ProgressDemo { progress ->
        AnimatedProgressBar(
            progress = progress,
            modifier = modifier
        )
    }
}

@Composable
private fun ProgressDemo(
    content: @Composable (newProgress: Float) -> Unit
) {
    var progress by remember { mutableFloatStateOf(getRandomProgress()) }

    LaunchedEffect(Unit) {
        while (this.isActive) {
            delay(1000)
            progress = getRandomProgress()
        }
    }

    content(progress)
}

private fun getRandomProgress(): Float {
    return Random.nextFloat() * (0.8f - 0.2f) + 0.2f
}

@Composable
private fun ProgressBarStyleItem(
    title: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    progressBarContent: @Composable () -> Unit
) {
    val borderStroke = when {
        selected -> BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        else -> BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground)
    }
    Column(
        modifier = modifier
            .border(borderStroke, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(color = MaterialTheme.colorScheme.primary),
                onClick = onClick
            )
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row {
            Text(
                text = title,
                maxLines = 1,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Rounded.CheckCircleOutline,
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = "Selected mark",
                modifier = Modifier.alpha(if (selected) 1f else 0f)
            )
        }

        progressBarContent()
    }
}