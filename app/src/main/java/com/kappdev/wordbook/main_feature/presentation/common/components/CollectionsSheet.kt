package com.kappdev.wordbook.main_feature.presentation.common.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kappdev.wordbook.core.presentation.common.CustomModalBottomSheet
import com.kappdev.wordbook.main_feature.domain.model.CollectionPreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionsSheet(
    selected: CollectionPreview?,
    collections: List<CollectionPreview>,
    onDismiss: () -> Unit,
    onSelect: (new: CollectionPreview) -> Unit
) {
    CustomModalBottomSheet(
        onDismissRequest = onDismiss
    ) { triggerDismiss ->
        LazyColumn(
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(collections) { collection ->
                CollectionPreviewItem(
                    item = collection,
                    selected = (collection.id == selected?.id),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSelect(collection)
                            triggerDismiss()
                        }
                        .padding(vertical = 8.dp, horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun CollectionPreviewItem(
    item: CollectionPreview,
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CollectionPreviewText(
            item.name,
            selected,
            Modifier.weight(1f)
        )
        CollectionPreviewText(item.cardsCount.toString(), selected)
    }
}

@Composable
private fun CollectionPreviewText(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    val color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
    val weight = if (selected) FontWeight.Bold else FontWeight.Medium

    Text(
        text = text,
        maxLines = 1,
        color = color,
        fontSize = 16.sp,
        fontWeight = weight,
        modifier = modifier,
        overflow = TextOverflow.Ellipsis,
    )
}

