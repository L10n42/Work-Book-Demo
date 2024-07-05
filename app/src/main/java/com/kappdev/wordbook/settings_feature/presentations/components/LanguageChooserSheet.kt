package com.kappdev.wordbook.settings_feature.presentations.components

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
import com.kappdev.wordbook.core.presentation.util.getCurrentAppLocale
import com.kappdev.wordbook.settings_feature.domain.AppLanguage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageChooserSheet(
    onDismiss: () -> Unit,
    onLanguageChange: (AppLanguage) -> Unit
) {
    val currentLocale = getCurrentAppLocale()
    CustomModalBottomSheet(
        onDismissRequest = onDismiss
    ) { _ ->
        LazyColumn(
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(AppLanguage.entries) { language ->
                AppLanguage(
                    language = language,
                    selected = (language.locale == currentLocale),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onLanguageChange(language)
                            onDismiss()
                        }
                        .padding(vertical = 8.dp, horizontal = 16.dp)
                )
            }
        }
    }
}


@Composable
private fun AppLanguage(
    language: AppLanguage,
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = language.flag, fontSize = 18.sp)
        LanguageText(language.nameToDisplay, selected)
    }
}

@Composable
private fun LanguageText(
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
