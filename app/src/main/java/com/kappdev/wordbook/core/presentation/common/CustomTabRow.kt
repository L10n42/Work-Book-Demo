package com.kappdev.wordbook.core.presentation.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun TabRowTextItem(
    text: String,
    isSelected: Boolean,
    textColor: Color = MaterialTheme.colorScheme.onBackground,
    selectedTextColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    val fontWeight by animateIntAsState(
        targetValue = if (isSelected) FontWeight.Bold.weight else FontWeight.Normal.weight,
        label = "FontWeighAnimation"
    )

    val fontColor by animateColorAsState(
        targetValue = if (isSelected) selectedTextColor else textColor,
        label = "FontColorAnimation"
    )

    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 12.dp),
        fontSize = 16.sp,
        fontWeight = FontWeight(fontWeight),
        textAlign = TextAlign.Center,
        color = fontColor
    )
}

@Composable
fun CustomTabRow(
    selectedTabIndex: Int,
    modifier: Modifier = Modifier,
    containerShape: Shape = RectangleShape,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    indicator: @Composable (tabPositions: List<TabPosition>, rowHeight: Dp) -> Unit = @Composable { tabPositions, height ->
        if (selectedTabIndex < tabPositions.size) {
            TabRowIndicator(
                height = height,
                modifier = Modifier
                    .tabIndicatorOffset(tabPositions[selectedTabIndex])
            )
        }
    },
    tabs: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.selectableGroup(),
        color = containerColor,
        shape = containerShape,
        contentColor = contentColor
    ) {
        SubcomposeLayout(Modifier.fillMaxWidth()) { constraints ->
            val tabRowWidth = constraints.maxWidth
            val tabMeasurables = subcompose(TabSlots.Tabs, tabs)
            val tabCount = tabMeasurables.size
            var tabWidth = 0
            if (tabCount > 0) {
                tabWidth = (tabRowWidth / tabCount)
            }
            val tabRowHeight = tabMeasurables.fold(initial = 0) { max, curr ->
                maxOf(curr.maxIntrinsicHeight(tabWidth), max)
            }

            val tabPlaceables = tabMeasurables.map {
                it.measure(
                    constraints.copy(
                        minWidth = tabWidth,
                        maxWidth = tabWidth,
                        minHeight = tabRowHeight,
                        maxHeight = tabRowHeight,
                    )
                )
            }

            val tabPositions = List(tabCount) { index ->
                TabPosition(tabWidth.toDp() * index, tabWidth.toDp())
            }

            layout(tabRowWidth, tabRowHeight) {
                subcompose(TabSlots.Indicator) {
                    indicator(tabPositions, tabRowHeight.toDp())
                }.forEach {
                    it.measure(Constraints.fixed(tabRowWidth, tabRowHeight)).placeRelative(0, 0)
                }

                tabPlaceables.forEachIndexed { index, placeable ->
                    placeable.placeRelative(index * tabWidth, 0)
                }
            }
        }
    }
}

@Composable
fun TabRowIndicator(
    modifier: Modifier = Modifier,
    height: Dp = 50.dp,
    shape: Shape = RectangleShape,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Box(
        modifier
            .fillMaxWidth()
            .height(height)
            .background(color = color, shape = shape)
    )
}

fun Modifier.jumpyTabIndicatorOffset(
    tabPosition: TabPosition
): Modifier = composed(
    inspectorInfo = debugInspectorInfo {
        name = "tabIndicatorOffset"
        value = tabPosition
    }
) {
    val currentTabWidth = remember { Animatable(tabPosition.width, Dp.VectorConverter) }
    val indicatorOffset = remember { Animatable(tabPosition.left, Dp.VectorConverter) }

    LaunchedEffect(tabPosition) {
        if (tabPosition.left != indicatorOffset.value) {
            launch {
                currentTabWidth.animateTo(
                    targetValue = tabPosition.width,
                    animationSpec = jumpySpec(tabPosition.width, bounceFraction = 1.8f)
                )
            }
            launch {
                indicatorOffset.animateTo(
                    targetValue = tabPosition.left,
                    animationSpec = jumpySpec(tabPosition.left, bounceFraction = 0.2f)
                )
            }
        }
    }

    fillMaxWidth()
        .wrapContentSize(Alignment.BottomStart)
        .offset(x = indicatorOffset.value)
        .width(currentTabWidth.value)
}

private fun jumpySpec(target: Dp, bounceFraction: Float, duration: Int = 350) = keyframes {
    durationMillis = duration
    target * bounceFraction atFraction 0.45f using LinearEasing
    target atFraction 1f using LinearOutSlowInEasing
}

fun Modifier.tabIndicatorOffset(
    currentTabPosition: TabPosition
): Modifier = composed(
    inspectorInfo = debugInspectorInfo {
        name = "tabIndicatorOffset"
        value = currentTabPosition
    }
) {
    val currentTabWidth by animateDpAsState(
        targetValue = currentTabPosition.width,
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
        label = "TabWidthAnimation"
    )
    val indicatorOffset by animateDpAsState(
        targetValue = currentTabPosition.left,
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
        label = "IndicatorOffsetAnimation"
    )
    fillMaxWidth()
        .wrapContentSize(Alignment.BottomStart)
        .offset(x = indicatorOffset)
        .width(currentTabWidth)
}

data class TabPosition(val left: Dp, val width: Dp)

private enum class TabSlots {
    Indicator,
    Tabs
}