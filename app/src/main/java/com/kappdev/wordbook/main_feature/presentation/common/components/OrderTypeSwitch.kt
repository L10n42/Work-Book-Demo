package com.kappdev.wordbook.main_feature.presentation.common.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kappdev.wordbook.R
import com.kappdev.wordbook.core.presentation.common.CustomTabRow
import com.kappdev.wordbook.core.presentation.common.TabRowIndicator
import com.kappdev.wordbook.core.presentation.common.TabRowTextItem
import com.kappdev.wordbook.core.presentation.common.jumpyTabIndicatorOffset
import com.kappdev.wordbook.main_feature.domain.util.OrderType

@Composable
fun OrderTypeSwitch(
    selected: OrderType,
    modifier: Modifier = Modifier,
    onOrderTypeChange: (OrderType) -> Unit
) {
    val selectedTypeIndex = if (selected == OrderType.Ascending) 0 else 1

    CustomTabRow(
        selectedTabIndex = selectedTypeIndex,
        containerShape = RoundedCornerShape(16.dp),
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        indicator = { tabPositions, rowHeight ->
            TabRowIndicator(
                height = rowHeight,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.jumpyTabIndicatorOffset(tabPositions[selectedTypeIndex])
            )
        }
    ) {
        TabRowTextItem(
            text = stringResource(R.string.ascending),
            isSelected = (selected == OrderType.Ascending),
            selectedTextColor = MaterialTheme.colorScheme.onPrimary,
            onClick = {
                onOrderTypeChange(OrderType.Ascending)
            }
        )
        TabRowTextItem(
            text = stringResource(R.string.descending),
            isSelected = (selected == OrderType.Descending),
            selectedTextColor = MaterialTheme.colorScheme.onPrimary,
            onClick = {
                onOrderTypeChange(OrderType.Descending)
            }
        )
    }
}