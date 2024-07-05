package com.kappdev.wordbook.main_feature.presentation.common.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kappdev.wordbook.R
import com.kappdev.wordbook.core.presentation.common.VerticalSpace

@Composable
fun EmptySearchResult() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.art_search),
            contentDescription = "Empty Search Result Image",
            modifier = Modifier.size(100.dp)
        )
        VerticalSpace(8.dp)
        Text(
            text = stringResource(R.string.nothing_found),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 14.sp
        )
    }
}