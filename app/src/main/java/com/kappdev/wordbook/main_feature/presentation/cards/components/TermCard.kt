package com.kappdev.wordbook.main_feature.presentation.cards.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.SubcomposeAsyncImage
import com.kappdev.wordbook.core.domain.model.Card
import com.kappdev.wordbook.core.presentation.common.CardElevation
import com.kappdev.wordbook.core.presentation.common.CardShape
import com.kappdev.wordbook.core.presentation.common.Speaker
import com.kappdev.wordbook.main_feature.presentation.common.components.ErrorImage
import com.kappdev.wordbook.main_feature.presentation.common.components.LoadingImage

@Composable
fun TermCard(
    card: Card,
    modifier: Modifier = Modifier,
    onSpeak: (text: String) -> Unit,
    onClick: () -> Unit
) {
    val hasImage = remember(card) { card.image != null }

    ConstraintLayout(
        modifier
            .shadow(elevation = CardElevation, shape = CardShape)
            .background(MaterialTheme.colorScheme.surfaceVariant, CardShape)
            .clip(CardShape)
            .clickable(onClick = onClick)
    ) {
        val (term, definition, speaker, image, example, divider) = createRefs()

        if (card.image != null) {
            CardImage(
                image = card.image,
                hasExample = card.example.isNotEmpty(),
                modifier = Modifier.constrainAs(image) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    width = Dimension.value(100.dp)
                    height = Dimension.value(100.dp)
                }
            )
        }

        Term(
            term = card.term,
            modifier = Modifier.constrainAs(term) {
                top.linkTo(parent.top, 4.dp)
                end.linkTo(speaker.start)
                start.linkTo(
                    if (hasImage) image.end else parent.start, 8.dp
                )
                width = Dimension.fillToConstraints
            }
        )

        Definition(
            definition = card.definition,
            modifier = Modifier.constrainAs(definition) {
                top.linkTo(term.bottom)
                end.linkTo(parent.end, 8.dp)
                start.linkTo(term.start)
                if (card.image == null && card.example.isEmpty()) {
                    bottom.linkTo(parent.bottom, 8.dp)
                }
                width = Dimension.fillToConstraints
            }
        )

        Speaker(
            textToSpeak = card.term,
            textToShow = card.transcription,
            speak = onSpeak,
            modifier = Modifier.constrainAs(speaker) {
                top.linkTo(parent.top, 4.dp)
                end.linkTo(parent.end, 4.dp)
            }
        )

        if (card.example.isNotEmpty()) {
            ExampleDivider(
                modifier = Modifier.constrainAs(divider) {
                    top.linkTo(
                        if (hasImage) image.bottom else definition.bottom,
                        if (hasImage) 0.dp else 12.dp
                    )
                    val horizontalSpacing = if (hasImage) 0.dp else 8.dp
                    start.linkTo(parent.start, horizontalSpacing)
                    end.linkTo(parent.end, horizontalSpacing)
                    width = Dimension.fillToConstraints
                }
            )

            Example(
                example = card.example,
                modifier = Modifier.constrainAs(example) {
                    top.linkTo(divider.bottom, 4.dp)
                    start.linkTo(parent.start, 8.dp)
                    end.linkTo(parent.end, 8.dp)
                    bottom.linkTo(parent.bottom, 8.dp)
                    width = Dimension.fillToConstraints
                }
            )
        }
    }
}

@Composable
private fun CardImage(
    image: String,
    hasExample: Boolean,
    modifier: Modifier = Modifier
) {
    val shape = if (hasExample) RoundedCornerShape(bottomEnd = 16.dp) else RectangleShape

    SubcomposeAsyncImage(
        model = image,
        modifier = modifier
            .background(MaterialTheme.colorScheme.background.copy(0.5f), shape)
            .clip(shape),
        contentScale = ContentScale.Crop,
        contentDescription = "Card Image",
        error = { ErrorImage(Modifier.fillMaxSize()) },
        loading = { LoadingImage(Modifier.fillMaxSize()) }
    )
}

@Composable
private fun ExampleDivider(
    modifier: Modifier = Modifier
) {
    Box(
        modifier
            .height(0.7.dp)
            .background(
                color = MaterialTheme.colorScheme.onSurface.copy(0.25f),
                shape = CircleShape
            )
    )
}

@Composable
private fun Example(
    example: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = example,
        maxLines = 1,
        fontSize = 14.sp,
        modifier = modifier,
        color = MaterialTheme.colorScheme.onBackground,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Italic,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun Definition(
    definition: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = definition,
        maxLines = 3,
        fontSize = 14.sp,
        lineHeight = 16.sp,
        modifier = modifier,
        color = MaterialTheme.colorScheme.onSurface,
        fontWeight = FontWeight.Normal,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun Term(
    term: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = term,
        maxLines = 1,
        fontSize = 16.sp,
        modifier = modifier,
        color = MaterialTheme.colorScheme.onSurface,
        fontWeight = FontWeight.Medium,
        overflow = TextOverflow.Ellipsis
    )
}