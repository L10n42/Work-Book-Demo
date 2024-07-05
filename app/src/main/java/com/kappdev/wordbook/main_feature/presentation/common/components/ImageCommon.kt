package com.kappdev.wordbook.main_feature.presentation.common.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kappdev.wordbook.R

@Composable
fun LoadingImage(
    modifier: Modifier = Modifier
) {
    ImagePlaceholder(imageRes = R.drawable.art_dreamer, modifier) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 1.dp,
            strokeCap = StrokeCap.Round,
            modifier = Modifier.size(14.dp)
        )
    }
}

@Composable
fun ErrorImage(
    modifier: Modifier = Modifier
) {
    ImagePlaceholder(imageRes = R.drawable.art_images, modifier) {
        Text(
            text = "Error.",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 10.sp
        )
    }
}

@Composable
private fun ImagePlaceholder(
    @DrawableRes imageRes: Int,
    modifier: Modifier,
    additionalContent: @Composable () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Image(
            painter = painterResource(imageRes),
            contentDescription = "Image Placeholder",
            modifier = Modifier.fillMaxHeight(0.7f)
        )
        additionalContent()
    }
}