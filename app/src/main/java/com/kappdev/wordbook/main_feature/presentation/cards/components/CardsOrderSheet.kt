package com.kappdev.wordbook.main_feature.presentation.cards.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.SortByAlpha
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kappdev.wordbook.R
import com.kappdev.wordbook.core.presentation.common.CustomModalBottomSheet
import com.kappdev.wordbook.main_feature.domain.util.CardsOrder
import com.kappdev.wordbook.main_feature.presentation.common.components.OrderItem
import com.kappdev.wordbook.main_feature.presentation.common.components.OrderTypeSwitch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardsOrderSheet(
    order: CardsOrder,
    onDismiss: () -> Unit,
    onOrderChanged: (newOrder: CardsOrder) -> Unit
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
                    title = stringResource(R.string.by_term),
                    selected = (order is CardsOrder.Term),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onOrderChanged(CardsOrder.Term(order.type))
                    }
                )

                OrderItem(
                    icon = Icons.Rounded.Event,
                    title = stringResource(R.string.by_creation),
                    selected = (order is CardsOrder.Created),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onOrderChanged(CardsOrder.Created(order.type))
                    }
                )

                OrderItem(
                    icon = Icons.Rounded.Schedule,
                    title = stringResource(R.string.by_last_edit),
                    selected = (order is CardsOrder.LastEdit),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onOrderChanged(CardsOrder.LastEdit(order.type))
                    }
                )
            }
        }
    }
}