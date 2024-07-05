package com.kappdev.wordbook.main_feature.presentation.common.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kappdev.wordbook.core.presentation.common.CustomModalBottomSheet
import com.kappdev.wordbook.main_feature.presentation.common.ImageSource
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageSourceChooser(
    onDismiss: () -> Unit,
    onClick: (imageSource: ImageSource) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(sheetState.currentValue, sheetState.targetValue) {
        if (sheetState.targetValue != SheetValue.Hidden && sheetState.currentValue == SheetValue.Hidden) {
            delay(150)
            isVisible = true
        } else {
            isVisible = sheetState.targetValue != SheetValue.Hidden
        }
    }

    CustomModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss
    ) { triggerDismiss ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ImageSource.entries.forEach { imageSource ->
                ImageSourceItem(
                    icon = imageSource.icon,
                    isVisible = isVisible,
                    title = stringResource(imageSource.titleRes),
                    onClick = {
                        onClick(imageSource)
                        triggerDismiss()
                    }
                )
            }
        }
    }
}

@Composable
private fun ImageSourceItem(
    icon: ImageVector,
    title: String,
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
    onClick: () -> Unit
) {
    val interSource = remember { MutableInteractionSource() }
    val pressed by interSource.collectIsPressedAsState()

    val animShadow by animateDpAsState(
        targetValue = if (pressed || !isVisible) 0.dp else 5.dp,
        label = "Shadow Animation"
    )

    val animBorder by animateFloatAsState(
        targetValue = if (pressed || !isVisible) 0.25f else 0f,
        label = "Border Alpha Animation"
    )

    val animBackground by animateColorAsState(
        targetValue = if (pressed || !isVisible) {
            MaterialTheme.colorScheme.surfaceTint
        } else {
            MaterialTheme.colorScheme.surface
        },
        label = "Background Color Animation"
    )

    Column(
        modifier = modifier
            .size(82.dp)
            .shadow(animShadow, shape)
            .background(animBackground, shape)
            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(animBorder), shape)
            .clip(shape)
            .clickable(
                interactionSource = interSource,
                indication = LocalIndication.current,
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