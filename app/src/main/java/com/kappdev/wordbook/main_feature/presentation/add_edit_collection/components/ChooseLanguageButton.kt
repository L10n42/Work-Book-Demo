package com.kappdev.wordbook.main_feature.presentation.add_edit_collection.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.kappdev.wordbook.core.presentation.common.ChooserButton

@Composable
fun ChooseLanguageButton(
    label: String,
    selected: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    ChooserButton(
        label = label,
        modifier = modifier,
        onClick = onClick
    ) {
        Text(
            text = selected,
            maxLines = 1,
            fontSize = 16.sp,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}