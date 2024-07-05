package com.kappdev.wordbook.share_feature.presentation.card_share.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import com.kappdev.wordbook.share_feature.presentation.card_share.CardShareStyle

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShareStyleChooser(
    selected: CardShareStyle,
    modifier: Modifier = Modifier,
    onSelect: (CardShareStyle) -> Unit
) {
    val listState = rememberLazyListState()
    val snapBehavior = rememberSnapFlingBehavior(listState)
    val itemWidths = remember { MutableList(CardShareStyle.entries.size) { 0 } }

    LaunchedEffect(selected) {
        val index = CardShareStyle.entries.indexOf(selected)
        val halfRowWidth = (listState.layoutInfo.viewportSize.width / 2)
        val halfItemWidth = itemWidths.getOrElse(index) { 0 } / 2
        val offset = halfRowWidth - halfItemWidth - listState.layoutInfo.mainAxisItemSpacing
        listState.animateScrollToItem(index, scrollOffset = -offset)
    }

    LazyRow(
        state = listState,
        modifier = modifier,
        flingBehavior = snapBehavior,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(CardShareStyle.entries.toTypedArray()) { index, style ->
            StyleItem(
                style = style,
                isSelected = (style == selected),
                onClick = { onSelect(style) },
                modifier = Modifier.onSizeChanged { itemSize ->
                    itemWidths.add(index, itemSize.width)
                }
            )
        }
    }
}

@Composable
private fun StyleItem(
    style: CardShareStyle,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp),
    onClick: () -> Unit
) {
    val interSource = remember { MutableInteractionSource() }
    val pressed by interSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = when {
            pressed -> 0.9f
            isSelected -> 1.1f
            else -> 1f
        },
        label = "Item scale animation",
        animationSpec = tween()
    )

    val shadowElevation by animateDpAsState(
        targetValue = if (pressed) 0.dp else 4.dp,
        label = "Shadow elevation animation",
        animationSpec = tween()
    )

    val shadowColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Black,
        label = "Shadow color animation",
        animationSpec = tween()
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            isSelected -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.onSurface.copy(0.25f)
        },
        label = "Border color animation",
        animationSpec = tween()
    )

    Canvas(
        modifier = modifier
            .size(45.dp)
            .scale(scale)
            .shadow(
                elevation = shadowElevation,
                shape = shape,
                spotColor = shadowColor,
                ambientColor = shadowColor
            )
            .border(1.dp, borderColor, shape)
            .clip(shape)
            .clickable(
                interactionSource = interSource,
                indication = LocalIndication.current,
                onClick = onClick
            )
    ) {
        val width = size.width
        val height = size.height

        val trianglePath = Path().apply {
            moveTo(0f, 0f)
            lineTo(width, 0f)
            lineTo(0f, height)
            close()
        }

        drawRect(style.centerColor)
        drawPath(trianglePath, style.sideColor)
    }
}