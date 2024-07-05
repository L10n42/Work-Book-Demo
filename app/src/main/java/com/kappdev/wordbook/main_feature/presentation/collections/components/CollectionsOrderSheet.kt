package com.kappdev.wordbook.main_feature.presentation.collections.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.Numbers
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.SortByAlpha
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kappdev.wordbook.R
import com.kappdev.wordbook.core.presentation.common.CustomModalBottomSheet
import com.kappdev.wordbook.main_feature.domain.util.CollectionsOrder
import com.kappdev.wordbook.main_feature.presentation.common.components.OrderItem
import com.kappdev.wordbook.main_feature.presentation.common.components.OrderTypeSwitch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionsOrderSheet(
    order: CollectionsOrder,
    onDismiss: () -> Unit,
    onOrderChanged: (newOrder: CollectionsOrder) -> Unit
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
            OrderTypeSwitch(
                selected = order.type,
                modifier = Modifier.fillMaxWidth(),
                onOrderTypeChange = { orderType ->
                    onOrderChanged(order.copy(orderType))
                }
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OrderItem(
                    icon = Icons.Rounded.SortByAlpha,
                    title = stringResource(R.string.by_name),
                    selected = (order is CollectionsOrder.Name),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onOrderChanged(CollectionsOrder.Name(order.type))
                    }
                )

                OrderItem(
                    icon = Icons.Rounded.Event,
                    title = stringResource(R.string.by_creation),
                    selected = (order is CollectionsOrder.Created),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onOrderChanged(CollectionsOrder.Created(order.type))
                    }
                )

                OrderItem(
                    icon = Icons.Rounded.Schedule,
                    title = stringResource(R.string.by_last_edit),
                    selected = (order is CollectionsOrder.LastEdit),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onOrderChanged(CollectionsOrder.LastEdit(order.type))
                    }
                )

                OrderItem(
                    icon = Icons.Rounded.Numbers,
                    title = stringResource(R.string.by_cards_number),
                    selected = (order is CollectionsOrder.Cards),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onOrderChanged(CollectionsOrder.Cards(order.type))
                    }
                )
            }
        }
    }
}