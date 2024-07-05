package com.kappdev.wordbook.study_feature.presentation.common.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.kappdev.wordbook.R
import com.kappdev.wordbook.core.domain.util.vibrateFor
import com.kappdev.wordbook.core.presentation.common.TypewriteText
import com.kappdev.wordbook.core.presentation.common.VerticalSpace
import com.kappdev.wordbook.settings_feature.domain.LocalAppSettings
import com.kappdev.wordbook.settings_feature.domain.isAllowed
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CongratulationView(
    modifier: Modifier = Modifier,
    onStudyAgain: () -> Unit,
    onLeave: () -> Unit
) {
    val appSettings = LocalAppSettings.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var writeText by remember { mutableStateOf(false) }
    var showButtons by remember { mutableStateOf(false) }
    var playFirework by remember { mutableStateOf(false) }

    val trophyScale = remember { Animatable(0f) }
    val trophyAlpha = remember { Animatable(0f) }

    fun showTrophy() = scope.launch {
        launch {
            trophyScale.animateTo(1.3f, tween(400))
            trophyScale.animateTo(1f, tween(200))
        }
        launch {
            trophyAlpha.animateTo(1f, tween(200))
        }
    }

    LaunchedEffect(Unit) {
        showTrophy().join()
        if (appSettings.vibration.isAllowed()) {
            context.vibrateFor(400)
        }
        writeText = true
        playFirework = true
        delay(300)
        showButtons = true
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Firework(
            isPlaying = playFirework,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        )

        Column(
            modifier = Modifier.matchParentSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.trophy),
                contentDescription = "Congratulation trophy",
                modifier = Modifier
                    .scale(trophyScale.value)
                    .alpha(trophyAlpha.value)
                    .size(300.dp)
            )

            TypewriteCongratulation(isVisible = writeText)

            VerticalSpace(72.dp)

            FinishButtons(
                isButtonsVisible = showButtons,
                onStudyAgain = onStudyAgain,
                onLeave = onLeave
            )
        }
    }
}

@Composable
private fun Firework(
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val lottieComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.firework_animation))
    LottieAnimation(
        composition = lottieComposition,
        isPlaying = isPlaying,
        iterations = 1,
        modifier = modifier
    )
}

@Composable
private fun TypewriteCongratulation(
    isVisible: Boolean,
    message: String = stringResource(R.string.study_congratulation)
) {
    TypewriteText(
        text = message,
        isVisible = isVisible,
        preoccupySpace = false,
        spec = tween(durationMillis = message.length * 30, easing = FastOutLinearInEasing),
        style = LocalTextStyle.current.copy(
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Composable
private fun FinishButtons(
    isButtonsVisible: Boolean,
    modifier: Modifier = Modifier,
    onStudyAgain: () -> Unit,
    onLeave: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FinishButton(
            isVisible = isButtonsVisible,
            text = stringResource(R.string.study_again),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            borderStroke = null,
            onClick = onStudyAgain
        )
        FinishButton(
            isVisible = isButtonsVisible,
            text = stringResource(R.string.leave),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary
            ),
            borderStroke = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
            onClick = onLeave
        )
    }
}

@Composable
private fun FinishButton(
    isVisible: Boolean,
    text: String,
    colors: ButtonColors,
    borderStroke: BorderStroke?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val slideSpec = tween<IntOffset>(400, easing = LinearEasing)
    val fadeSpec = tween<Float>(400, easing = LinearEasing)

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(fadeSpec) + slideInVertically(slideSpec) { it },
        exit = slideOutHorizontally(slideSpec) { it } + fadeOut(fadeSpec)
    ) {
        Button(
            onClick = onClick,
            modifier = modifier
                .width(300.dp)
                .height(50.dp),
            shape = RoundedCornerShape(8.dp),
            colors = colors,
            border = borderStroke
        ) {
            Text(
                text = text,
                fontSize = 16.sp
            )
        }
    }
}