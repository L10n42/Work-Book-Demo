package com.kappdev.wordbook.core.presentation.common

import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
@ExperimentalMaterial3Api
fun CustomModalBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    skipPartiallyExpanded: Boolean = false,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded),
    shape: Shape = BottomSheetDefaults.ExpandedShape,
    containerColor: Color = MaterialTheme.colorScheme.surfaceTint,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    tonalElevation: Dp = 0.dp,
    scrimColor: Color = BottomSheetDefaults.ScrimColor,
    dragHandle: @Composable (() -> Unit)? = {
        BottomSheetDefaults.DragHandle(color = MaterialTheme.colorScheme.primary)
    },
    windowInsets: WindowInsets = BottomSheetDefaults.windowInsets,
    content: @Composable ColumnScope.(triggerDismiss: () -> Unit) -> Unit,
) {
    val inPortraitMode = LocalConfiguration.current.orientation == ORIENTATION_PORTRAIT
    val scope = rememberCoroutineScope()

    fun dismissWithAnimation() {
        scope.launch {
            sheetState.hide()
            onDismissRequest()
        }
    }

    androidx.compose.material3.ModalBottomSheet(
        onDismissRequest,
        modifier,
        sheetState,
        shape,
        containerColor,
        contentColor,
        tonalElevation,
        scrimColor,
        dragHandle,
        windowInsets,
        content = {
            Column(
                modifier = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && inPortraitMode) {
                    Modifier.padding(bottom = StaticNavigationBarPadding)
                } else {
                    Modifier.navigationBarsPadding()
                },
                content = {
                    content(::dismissWithAnimation)
                }
            )
        }
    )
}

private val StaticNavigationBarPadding = 50.dp