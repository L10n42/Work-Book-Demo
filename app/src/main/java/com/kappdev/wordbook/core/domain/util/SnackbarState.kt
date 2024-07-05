package com.kappdev.wordbook.core.domain.util

import android.content.res.Resources
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class SnackbarState(
    private val scope: CoroutineScope
) {
    private val _messageRes = MutableSharedFlow<Int>()
    val message: SharedFlow<Int> = _messageRes

    fun show(resId: Int) {
        scope.launch { _messageRes.emit(resId) }
    }
}

@Composable
fun SnackbarStateHandler(
    state: SnackbarState,
    hostState: SnackbarHostState,
    resources: Resources = LocalContext.current.resources
) {
    LaunchedEffect(state.message) {
        state.message.collect { messageResId ->
            hostState.showSnackbar(resources.getString(messageResId))
        }
    }
}