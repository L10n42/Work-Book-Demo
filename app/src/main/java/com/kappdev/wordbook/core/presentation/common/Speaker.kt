package com.kappdev.wordbook.core.presentation.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties

@Composable
fun Speaker(
    textToSpeak: String,
    textToShow: String,
    modifier: Modifier = Modifier,
    speak: (text: String) -> Unit
) {
    val (iconSize, updateIconSize) = remember { mutableStateOf(IntSize.Zero) }
    val showMessage = remember { MutableTransitionState(false) }

    Box(modifier) {
        Icon(
            imageVector = Icons.Rounded.VolumeUp,
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = "Speaker",
            modifier = Modifier
                .onSizeChanged(updateIconSize)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        if (textToShow.isNotBlank()) {
                            showMessage.targetState = true
                        }
                        speak(textToSpeak)
                    }
                )
        )

        if (showMessage.currentState || showMessage.targetState || !showMessage.isIdle) {
            Popup(
                alignment = Alignment.BottomEnd,
                onDismissRequest = { showMessage.targetState = false },
                offset = IntOffset(-iconSize.width / 2, -iconSize.height),
                properties = PopupProperties(focusable = true)
            ) {
                val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
                AnimatedVisibility(
                    visibleState = showMessage,
                    enter = scaleIn(
                        transformOrigin = if (isRtl) TransformOrigin.BottomLeft else TransformOrigin.BottomRight
                    ),
                    exit = scaleOut(
                        transformOrigin = if (isRtl) TransformOrigin.BottomLeft else TransformOrigin.BottomRight
                    )
                ) {
                    Text(
                        text = textToShow,
                        fontSize = 16.sp,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .shadow(elevation = 6.dp, shape = MessageShape)
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = MessageShape
                            )
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}

private val TransformOrigin.Companion.BottomRight: TransformOrigin
    get() = TransformOrigin(1f, 1f)

private val TransformOrigin.Companion.BottomLeft: TransformOrigin
    get() = TransformOrigin(0f, 1f)

private val MessageShape = RoundedCornerShape(12.dp, 12.dp, 0.dp, 12.dp)