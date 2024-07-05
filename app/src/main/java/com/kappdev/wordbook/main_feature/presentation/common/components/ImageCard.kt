package com.kappdev.wordbook.main_feature.presentation.common.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.kappdev.wordbook.R
import com.kappdev.wordbook.core.presentation.common.AutoMirroredIcon
import com.kappdev.wordbook.core.presentation.common.FieldShape
import com.kappdev.wordbook.main_feature.domain.util.Image
import com.kappdev.wordbook.main_feature.domain.util.isNotEmptyOrDeleted
import com.kappdev.wordbook.main_feature.presentation.common.ImageSource
import kotlinx.coroutines.delay

@Composable
fun ImageCard(
    image: Image,
    title: String,
    modifier: Modifier = Modifier,
    onPick: (source: ImageSource) -> Unit,
    onDelete: () -> Unit,
) {
    var showChooser by remember { mutableStateOf(false) }
    if (showChooser) {
        ImageSourceChooser(
            onDismiss = { showChooser = false },
            onClick = onPick
        )
    }

    Column(
        modifier
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = FieldShape
            )
            .clip(FieldShape)
            .clickable(enabled = !image.isNotEmptyOrDeleted()) { showChooser = true },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(start = 10.dp, top = 10.dp, bottom = 10.dp, end = 0.dp)
        ) {
            ImageIcon()
            ImageTitle(
                title = title,
                Modifier.weight(1f)
            )
            ActionButton(
                state = if (image.isNotEmptyOrDeleted()) ButtonState.RemoveImage else ButtonState.PickImage,
                onClick = {
                    if (image.isNotEmptyOrDeleted()) onDelete() else showChooser = true
                }
            )
        }

        AnimatedImageContent(
            image = image,
            Modifier.padding(start = 12.dp, top = 0.dp, bottom = 12.dp, end = 12.dp)
        )
    }
}

@Composable
private fun AnimatedImageContent(
    image: Image,
    modifier: Modifier = Modifier
) {
    var imageToDisplay by remember { mutableStateOf<Image>(Image.Empty) }

    LaunchedEffect(image) {
        if (image.isNotEmptyOrDeleted()) {
            imageToDisplay = image
        } else {
            delay(700)
            imageToDisplay = image
        }
    }

    AnimatedVisibility(
        visible = image.isNotEmptyOrDeleted(),
        modifier = modifier,
        enter = expandVertically(
            tween(durationMillis = 400), expandFrom = Alignment.Top
        ) + fadeIn(
            tween(durationMillis = 300, delayMillis = 400)
        ),
        exit = fadeOut(
            tween(durationMillis = 300)
        ) + shrinkVertically(
            tween(durationMillis = 400, delayMillis = 300), shrinkTowards = Alignment.Top
        )
    ) {
        SubcomposeAsyncImage(
            model = imageToDisplay.takeIf(Image::isNotEmptyOrDeleted)?.model,
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.background.copy(0.5f),
                    shape = RoundedCornerShape(12.dp)
                )
                .clip(RoundedCornerShape(12.dp)),
            contentDescription = "Image",
            error = { ErrorImage(
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f)) },
            loading = { LoadingImage(
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f)) }
        )
    }
}

@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
private fun ActionButton(
    state: ButtonState,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val avdIcon = AnimatedImageVector.animatedVectorResource(R.drawable.avd_chevron_to_close)
    Icon(
        painter = rememberAnimatedVectorPainter(animatedImageVector = avdIcon, atEnd = (state == ButtonState.RemoveImage)),
        tint = MaterialTheme.colorScheme.primary,
        contentDescription = null,
        modifier = modifier
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .padding(4.dp)
    )
}

private enum class ButtonState { RemoveImage, PickImage }

@Composable
private fun ImageTitle(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        maxLines = 1,
        fontSize = 16.sp,
        overflow = TextOverflow.Ellipsis,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
    )
}

@Composable
private fun ImageIcon(
    modifier: Modifier = Modifier
) {
    AutoMirroredIcon(
        modifier = modifier,
        imageVector = Icons.Rounded.Image,
        contentDescription = "Image Icon",
        tint = MaterialTheme.colorScheme.onSurface
    )
}