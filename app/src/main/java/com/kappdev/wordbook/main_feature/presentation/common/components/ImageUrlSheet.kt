package com.kappdev.wordbook.main_feature.presentation.common.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.kappdev.wordbook.R
import com.kappdev.wordbook.core.presentation.common.InputField
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ImageUrlSheet(
    onDismiss: () -> Unit,
    onDownload: (imageUrl: String) -> Unit
) {
    val (url, updateUrl) = remember { mutableStateOf("") }

    UrlDialog(onDismiss) { triggerDismiss ->
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            InputField(
                value = url,
                singleLine = true,
                onValueChange = updateUrl,
                label = stringResource(R.string.image_url),
                hint = stringResource(R.string.enter_image_url),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = {
                    if (url.isNotBlank()) {
                        onDownload(url)
                    }
                    triggerDismiss()
                }
            ) {
                Text(text = stringResource(R.string.download))
            }
        }
    }
}

@Composable
private fun UrlDialog(
    onDismiss: () -> Unit,
    duration: Int = 450,
    content: @Composable (triggerDismiss: () -> Unit) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val transitionState = remember {
        MutableTransitionState(false).apply { targetState = true }
    }

    fun dismissWithAnimation() = scope.launch {
        transitionState.targetState = false
        delay(duration.toLong())
        onDismiss()
    }

    Dialog(onDismissRequest = ::dismissWithAnimation) {
        AnimatedVisibility(
            visibleState = transitionState,
            enter = fadeIn(tween(duration)) + scaleIn(tween(duration)),
            exit = scaleOut(tween(duration)) + fadeOut((tween(duration))),
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                content = {
                    content(::dismissWithAnimation)
                }
            )
        }
    }
}