package com.kappdev.wordbook.study_feature.presentation.flashcards.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.SubcomposeAsyncImage
import com.kappdev.wordbook.core.presentation.common.Speaker
import com.kappdev.wordbook.main_feature.presentation.common.components.ErrorImage
import com.kappdev.wordbook.main_feature.presentation.common.components.LoadingImage

@Composable
fun BackSideCard(
    imagePath: String?,
    definition: String,
    example: String,
    modifier: Modifier = Modifier,
    onSpeak: (String) -> Unit
) {
    ConstraintLayout(modifier) {
        val (defText, speaker, image, exampleText) = createRefs()

        Speaker(
            textToSpeak = definition,
            textToShow = "",
            speak = onSpeak,
            modifier = Modifier.constrainAs(speaker) {
                end.linkTo(parent.end)
                top.linkTo(parent.top)
            }
        )

        if (imagePath != null) {
            CardImage(
                image = imagePath,
                modifier = Modifier.constrainAs(image) {
                    start.linkTo(parent.start, 16.dp)
                    end.linkTo(parent.end, 16.dp)
                    top.linkTo(speaker.bottom, 16.dp)
                    width = Dimension.value(200.dp)
                    height = Dimension.value(200.dp)
                }
            )
        }

        Definition(
            definition = definition,
            modifier = Modifier.constrainAs(defText) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)

                if (imagePath == null) {
                    top.linkTo(speaker.bottom)
                    bottom.linkTo(exampleText.top)
                } else {
                    top.linkTo(image.bottom, 16.dp)
                }
            }
        )

        Example(
            example = example,
            modifier = Modifier.constrainAs(exampleText) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            }
        )
    }
}

@Composable
private fun Example(
    example: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = example,
        maxLines = 3,
        fontSize = 14.sp,
        lineHeight = 16.sp,
        modifier = modifier,
        textAlign = TextAlign.Center,
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
        maxLines = 5,
        fontSize = 16.sp,
        lineHeight = 18.sp,
        modifier = modifier,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurface,
        fontWeight = FontWeight.Normal,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun CardImage(
    image: String,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)
    SubcomposeAsyncImage(
        model = image,
        modifier = modifier
            .background(MaterialTheme.colorScheme.background.copy(0.5f), shape)
            .clip(shape),
        contentScale = ContentScale.Crop,
        contentDescription = "BackSideCardImage",
        error = { ErrorImage(Modifier.fillMaxSize()) },
        loading = { LoadingImage(Modifier.fillMaxSize()) }
    )
}