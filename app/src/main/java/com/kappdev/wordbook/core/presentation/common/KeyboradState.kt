package com.kappdev.wordbook.core.presentation.common

import android.graphics.Rect
import android.view.ViewTreeObserver
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import kotlinx.coroutines.CoroutineScope

enum class Keyboard {
    Shown, Hidden
}

@Composable
fun keyboardAsState(): State<Keyboard> {
    val keyboardState = remember { mutableStateOf(Keyboard.Hidden) }
    val view = LocalView.current
    DisposableEffect(view) {
        val onGlobalListener = ViewTreeObserver.OnGlobalLayoutListener {
            val rect = Rect()
            view.getWindowVisibleDisplayFrame(rect)
            val screenHeight = view.rootView.height
            val keypadHeight = screenHeight - rect.bottom
            keyboardState.value = if (keypadHeight > screenHeight * 0.15) {
                Keyboard.Shown
            } else {
                Keyboard.Hidden
            }
        }
        view.viewTreeObserver.addOnGlobalLayoutListener(onGlobalListener)

        onDispose {
            view.viewTreeObserver.removeOnGlobalLayoutListener(onGlobalListener)
        }
    }

    return keyboardState
}

@Composable
fun KeyboardStateChangeEffect(
    block: suspend CoroutineScope.(state: Keyboard) -> Unit
) {
    val keyboardState by keyboardAsState()

    LaunchedEffect(keyboardState) {
        block(keyboardState)
    }
}

@Composable
fun KeyboardHiddenEffect(
    block: suspend CoroutineScope.() -> Unit
) {
    val keyboardState by keyboardAsState()

    LaunchedEffect(keyboardState) {
        if (keyboardState == Keyboard.Hidden) {
            block()
        }
    }
}

@Composable
fun KeyboardShownEffect(
    block: suspend CoroutineScope.() -> Unit
) {
    val keyboardState by keyboardAsState()

    LaunchedEffect(keyboardState) {
        if (keyboardState == Keyboard.Shown) {
            block()
        }
    }
}