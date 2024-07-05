package com.kappdev.wordbook.share_feature.presentation.card_share.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.kappdev.wordbook.R
import com.kappdev.wordbook.core.domain.model.Card
import com.kappdev.wordbook.core.presentation.common.CardElevation
import com.kappdev.wordbook.core.presentation.common.CardShape
import com.kappdev.wordbook.main_feature.presentation.common.components.ErrorImage
import com.kappdev.wordbook.main_feature.presentation.common.components.LoadingImage
import com.kappdev.wordbook.share_feature.presentation.card_share.CardShareStyle

@Composable
fun ShareCardView(
    card: Card,
    style: CardShareStyle,
    showDivider: Boolean,
    modifier: Modifier = Modifier
) {
    Box(modifier) {
        GradientBackground(
            style = style,
            modifier = Modifier.matchParentSize()
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val cardModifier = Modifier
                .padding(horizontal = 16.dp, vertical = 32.dp)
                .fillMaxWidth()
                .shadow(elevation = CardElevation, shape = CardShape)
                .background(MaterialTheme.colorScheme.surfaceVariant, CardShape)
                .clip(CardShape)

            if (!card.image.isNullOrBlank()) {
                ShareCardWithImage(card, cardModifier)
            } else {
                ShareCardWithoutImage(card, cardModifier, showDivider)
            }

            Watermark(Modifier.padding(bottom = 8.dp))
        }
    }
}

@Composable
private fun ShareCardWithoutImage(
    card: Card,
    modifier: Modifier,
    showDivider: Boolean
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Term(
            term = card.term,
            textAlign = TextAlign.Center
        )
        if (showDivider) {
            Divider(Modifier.fillMaxWidth(0.9f))
        }
        Definition(
            definition = card.definition,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ShareCardWithImage(
    card: Card,
    modifier: Modifier
) {
    Row(modifier) {
        CardImage(
            image = card.image,
            modifier = Modifier.size(100.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Term(term = card.term)
            Definition(definition = card.definition)
        }
    }
}

@Composable
private fun Divider(
    modifier: Modifier = Modifier
) {
    Box(
        modifier
            .height(0.7.dp)
            .background(
                color = MaterialTheme.colorScheme.onSurface.copy(0.16f),
                shape = CircleShape
            )
    )
}

@Composable
private fun Watermark(
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(R.string.watermark),
        fontSize = 12.sp,
        modifier = modifier,
        color = Color.Black.copy(0.64f),
        fontWeight = FontWeight.Medium,
    )
}

@Composable
private fun GradientBackground(
    style: CardShareStyle,
    modifier: Modifier
) {
    val centerColor by animateColorAsState(
        targetValue = style.centerColor,
        label = "Center color animation"
    )

    val sideColor by animateColorAsState(
        targetValue = style.sideColor,
        label = "Side color animation"
    )

    Canvas(modifier = modifier) {
        drawRect(
            size = size,
            brush = Brush.radialGradient(
                listOf(centerColor, sideColor),
                radius = size.width * 0.95f
            )
        )
    }
}

@Composable
private fun CardImage(
    image: String?,
    modifier: Modifier = Modifier
) {
    SubcomposeAsyncImage(
        model = image,
        modifier = modifier.background(MaterialTheme.colorScheme.background.copy(0.5f)),
        contentScale = ContentScale.Crop,
        contentDescription = "Card Image",
        error = { ErrorImage(Modifier.fillMaxSize()) },
        loading = { LoadingImage(Modifier.fillMaxSize()) }
    )
}

@Composable
private fun Definition(
    definition: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign? = null,
) {
    Text(
        text = definition,
        maxLines = 4,
        fontSize = 14.sp,
        lineHeight = 16.sp,
        modifier = modifier,
        textAlign = textAlign,
        color = MaterialTheme.colorScheme.onSurface,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun Term(
    term: String,
    modifier: Modifier = Modifier,
    maxLines: Int = 1,
    textAlign: TextAlign? = null,
) {
    Text(
        text = term,
        maxLines = maxLines,
        fontSize = 16.sp,
        lineHeight = 18.sp,
        textAlign = textAlign,
        modifier = modifier,
        color = MaterialTheme.colorScheme.onSurface,
        fontWeight = FontWeight.Medium,
        overflow = TextOverflow.Ellipsis
    )
}