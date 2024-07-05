package com.kappdev.wordbook.study_feature.presentation.writing.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.kappdev.wordbook.core.presentation.common.ShimmerLoading
import com.kappdev.wordbook.core.presentation.common.VerticalSpace

@Composable
fun WritingLoading(
    modifier: Modifier
) {
    ShimmerLoading { shimmerBrush ->
        Column(
            modifier = modifier.padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            VerticalSpace(32.dp)
            LoadingQuestion(
                brush = shimmerBrush,
                modifier = Modifier.fillMaxWidth()
            )

            VerticalSpace(64.dp)

            LoadingAnswer(
                brush = shimmerBrush,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun LoadingQuestion(
    brush: Brush,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp)
) {
    Box(
        modifier
            .height(200.dp)
            .shadow(elevation = 4.dp, shape = shape)
            .background(MaterialTheme.colorScheme.surfaceVariant, shape)
            .background(brush, shape)
    )
}

@Composable
private fun LoadingAnswer(
    brush: Brush,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp)
) {
    Box(
        modifier
            .height(54.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, shape)
            .background(brush, shape)
    )
}