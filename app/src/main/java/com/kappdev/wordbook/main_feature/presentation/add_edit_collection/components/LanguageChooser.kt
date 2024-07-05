package com.kappdev.wordbook.main_feature.presentation.add_edit_collection.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.kappdev.wordbook.core.presentation.util.getCurrentAppLocale
import java.util.Locale

@Composable
fun LanguageChooser(
    label: String,
    selected: Locale,
    availableLocales: List<Locale>,
    modifier: Modifier = Modifier,
    onChange: (Locale) -> Unit
) {
    var showSheet by remember { mutableStateOf(false) }

    if (showSheet) {
        LanguagesSheet(
            selected = selected,
            availableLocales = availableLocales,
            onDismiss = { showSheet = false },
            onSelect = onChange
        )
    }

    val currentLocale = getCurrentAppLocale()
    val selectedName = currentLocale?.let { selected.getDisplayName(it) } ?: selected.displayName
    ChooseLanguageButton(
        label = label,
        selected = selectedName,
        onClick = { showSheet = true },
        modifier = modifier
    )
}