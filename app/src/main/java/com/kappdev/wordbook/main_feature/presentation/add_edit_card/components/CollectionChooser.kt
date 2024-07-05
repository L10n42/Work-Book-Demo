package com.kappdev.wordbook.main_feature.presentation.add_edit_card.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.kappdev.wordbook.R
import com.kappdev.wordbook.core.presentation.common.ChooserButton
import com.kappdev.wordbook.main_feature.domain.model.CollectionPreview
import com.kappdev.wordbook.main_feature.presentation.common.components.CollectionsSheet

@Composable
fun CollectionChooser(
    selected: CollectionPreview?,
    collections: List<CollectionPreview>,
    modifier: Modifier = Modifier,
    onChange: (CollectionPreview) -> Unit
) {
    var showSheet by remember { mutableStateOf(false) }

    if (showSheet) {
        CollectionsSheet(
            selected = selected,
            collections = collections,
            onDismiss = { showSheet = false },
            onSelect = onChange
        )
    }

    ChooserButton(
        label = stringResource(R.string.collection),
        modifier = modifier,
        onClick = { showSheet = true }
    ) {
        Text(
            text = selected?.name ?: "",
            maxLines = 1,
            fontSize = 16.sp,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}