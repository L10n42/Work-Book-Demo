package com.kappdev.wordbook.share_feature.presentation.common.components

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun BottomSheetLikeDialog(
    onDismiss: () -> Unit,
    surfaceColor: Color = MaterialTheme.colorScheme.surfaceTint,
    content: @Composable ColumnScope.(triggerDismiss: () -> Unit) -> Unit
) {
    val scope = rememberCoroutineScope()
    val transitionState = remember {
        MutableTransitionState(false).apply { targetState = true }
    }

    val dismissWithAnimation: () -> Unit = {
        scope.launch {
            transitionState.targetState = false
            delay(EXIT_DURATION.toLong())
            onDismiss()
        }
    }

    val scrimAlpha by animateFloatAsState(
        targetValue = if (transitionState.targetState) 0.32f else 0f,
        animationSpec = tween(
            if (transitionState.targetState) ENTER_DURATION else EXIT_DURATION
        ),
        label = "Dialog scrim alpha animation"
    )

    Dialog(
        onDismissRequest = dismissWithAnimation,
        properties = DialogProperties(decorFitsSystemWindows = false)
    ) {
        val activityWindow = getActivityWindow()
        val dialogWindow = (LocalView.current.parent as? DialogWindowProvider)?.window
        val parentView = LocalView.current.parent as View
        SideEffect {
            if (activityWindow != null && dialogWindow != null) {
                val attributes = WindowManager.LayoutParams()
                attributes.copyFrom(activityWindow.attributes)
                attributes.type = dialogWindow.attributes.type
                dialogWindow.attributes = attributes
                parentView.layoutParams = FrameLayout.LayoutParams(
                    activityWindow.decorView.width,
                    activityWindow.decorView.height
                )
            }
        }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(scrimAlpha))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = dismissWithAnimation
                    )
            )

            AnimatedVisibility(
                visibleState = transitionState,
                enter = fadeIn(tween(ENTER_DURATION)) + slideInVertically(tween(ENTER_DURATION)) { it },
                exit = slideOutVertically(tween(EXIT_DURATION)) { it } + fadeOut(tween(EXIT_DURATION)),
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {}
                )
            ) {
                val isLandscapeMode = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
                Column(
                    Modifier
                        .fillMaxWidth(
                            if (isLandscapeMode) 0.8f else 1f
                        )
                        .background(surfaceColor)
                        .then(
                            if (!isLandscapeMode) Modifier.navigationBarsPadding() else Modifier
                        ),
                    content = {
                        content(dismissWithAnimation)
                    }
                )
            }
        }
    }
}

@Composable
private fun getActivityWindow(): Window? = LocalView.current.context.getActivityWindow()

private tailrec fun Context.getActivityWindow(): Window? =
    when (this) {
        is Activity -> window
        is ContextWrapper -> baseContext.getActivityWindow()
        else -> null
    }


private const val ENTER_DURATION = 350
private const val EXIT_DURATION = 200