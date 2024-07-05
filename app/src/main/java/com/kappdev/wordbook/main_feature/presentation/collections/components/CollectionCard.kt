package com.kappdev.wordbook.main_feature.presentation.collections.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.ImageLoader
import coil.compose.AsyncImage
import com.kappdev.wordbook.R
import com.kappdev.wordbook.core.presentation.common.CardElevation
import com.kappdev.wordbook.core.presentation.common.CardShape
import com.kappdev.wordbook.core.presentation.common.PressedCardElevation
import com.kappdev.wordbook.main_feature.domain.model.CollectionInfo
import com.kappdev.wordbook.theme.Graphite

@Composable
fun CollectionCard(
    info: CollectionInfo,
    modifier: Modifier = Modifier,
    onMore: () -> Unit,
    onNewCard: () -> Unit,
    onClick: () -> Unit
) {
    val backgroundColor = info.color ?: MaterialTheme.colorScheme.surfaceVariant
    val textStyle = when {
        info.backgroundImage != null -> LocalTextStyle.current.copy(color = Color.White, shadow = CardTextShadow)
        info.color != null -> LocalTextStyle.current.copy(color = Graphite)
        else -> LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onSurface)
    }
    val interactionSource = remember { MutableInteractionSource() }
    val isCardPressed by interactionSource.collectIsPressedAsState()

    val cardElevation by animateDpAsState(
        targetValue = if (isCardPressed) PressedCardElevation else CardElevation,
        label = "Card Elevation"
    )

    ConstraintLayout(
        modifier
            .shadow(elevation = cardElevation, shape = CardShape)
            .background(backgroundColor, CardShape)
            .clip(CardShape)
            .clickable(interactionSource, indication = LocalIndication.current, onClick = onClick)
    ) {
        val (name, description, button, cardsCount, more, background) = createRefs()

        if (info.backgroundImage != null) {
            BackgroundImage(
                image = info.backgroundImage,
                modifier = Modifier.constrainAs(background) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }
            )
        }

        CollectionName(
            name = info.name,
            textStyle = textStyle,
            modifier = Modifier.constrainAs(name) {
                start.linkTo(parent.start, 16.dp)
                top.linkTo(parent.top, 12.dp)
                end.linkTo(more.start)
                width = Dimension.fillToConstraints
            }
        )

        CollectionDescription(
            description = info.description,
            textStyle = textStyle,
            modifier = Modifier.constrainAs(description) {
                start.linkTo(parent.start, 16.dp)
                top.linkTo(name.bottom)
                end.linkTo(more.start)
                width = Dimension.fillToConstraints
            }
        )

        ActionButton(
            modifier = Modifier.constrainAs(button) {
                start.linkTo(parent.start, 16.dp)
                bottom.linkTo(parent.bottom, 10.dp)
                top.linkTo(description.bottom, 16.dp)
            },
            onClick = onNewCard
        )

        CardsCount(
            count = info.cardsCount,
            textStyle = textStyle,
            modifier = Modifier.constrainAs(cardsCount) {
                end.linkTo(parent.end, 16.dp)
                bottom.linkTo(parent.bottom, 20.dp)
            }
        )

        CardMore(
            tint = when {
                info.backgroundImage != null -> Color.White
                info.color != null -> Graphite
                else -> MaterialTheme.colorScheme.onSurface
            },
            modifier = Modifier.constrainAs(more) {
                end.linkTo(parent.end)
                top.linkTo(parent.top)
            },
            onClick = onMore
        )
    }
}

private val CardTextShadow = Shadow(
    color = Color.Black.copy(0.7f),
    offset = Offset(0f, 2f),
    blurRadius = 4f
)

@Composable
private fun CollectionName(
    name: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current
) {
    Text(
        text = name,
        maxLines = 1,
        fontSize = 18.sp,
        modifier = modifier,
        fontWeight = FontWeight.SemiBold,
        overflow = TextOverflow.Ellipsis,
        style = textStyle
    )
}

@Composable
private fun CollectionDescription(
    description: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current
) {
    Text(
        text = description,
        maxLines = 2,
        fontSize = 16.sp,
        lineHeight = 18.sp,
        modifier = modifier,
        fontWeight = FontWeight.Medium,
        overflow = TextOverflow.Ellipsis,
        style = textStyle,
    )
}

@Composable
private fun ActionButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        shape = CircleShape,
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Text(
            text = stringResource(R.string.add_card),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun CardsCount(
    count: Int,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current
) {
    val textToDisplay = when (count) {
        0 -> stringResource(R.string.has_no_cards)
        1 -> stringResource(R.string.contains_single_card)
        else -> stringResource(R.string.contains_cards, count)
    }

    Text(
        text = textToDisplay,
        maxLines = 1,
        fontSize = 16.sp,
        modifier = modifier,
        fontWeight = FontWeight.Medium,
        overflow = TextOverflow.Ellipsis,
        style = textStyle,
    )
}

@Composable
private fun CardMore(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Rounded.MoreHoriz,
            contentDescription = "Card More",
            tint = tint
        )
    }
}


@Composable
private fun BackgroundImage(
    image: String,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = image,
        contentScale = ContentScale.Crop,
        modifier = modifier,
        contentDescription = "Collection background image",
        imageLoader = ImageLoader.Builder(LocalContext.current)
            .crossfade(true)
            .build()
    )
}