package com.kappdev.wordbook.main_feature.presentation.add_edit_collection.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kappdev.wordbook.core.presentation.common.CustomModalBottomSheet
import com.kappdev.wordbook.core.presentation.util.getCurrentAppLocale
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguagesSheet(
    selected: Locale,
    availableLocales: List<Locale>,
    onDismiss: () -> Unit,
    onSelect: (new: Locale) -> Unit
) {
    CustomModalBottomSheet(
        onDismissRequest = onDismiss
    ) { triggerDismiss ->
        LazyColumn(
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(availableLocales) { locale ->
                LanguageItem(
                    item = locale,
                    selected = (locale == selected),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSelect(locale)
                            triggerDismiss()
                        }
                        .padding(vertical = 8.dp, horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun LanguageItem(
    item: Locale,
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    val color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
    val weight = if (selected) FontWeight.Bold else FontWeight.Medium
    val currentLocale = getCurrentAppLocale()
    val name = currentLocale?.let { item.getDisplayName(it) } ?: item.displayName
    Text(
        text = name,
        fontSize = 16.sp,
        color = color,
        fontWeight = weight,
        modifier = modifier
    )
}