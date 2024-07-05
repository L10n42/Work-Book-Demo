package com.kappdev.wordbook.main_feature.presentation.collections.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kappdev.wordbook.core.presentation.common.CardShape
import com.kappdev.wordbook.core.presentation.common.FABPadding
import com.kappdev.wordbook.main_feature.presentation.common.components.ContentLoading

@Composable
fun CollectionsLoading() {
    ContentLoading { color ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
            contentPadding = PaddingValues(bottom = FABPadding, top = 16.dp, start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(10) {
                Box(
                    Modifier
                        .height(150.dp)
                        .fillMaxWidth()
                        .background(color, CardShape)
                )
            }
        }
    }
}