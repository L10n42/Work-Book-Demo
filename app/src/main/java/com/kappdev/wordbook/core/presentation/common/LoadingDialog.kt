package com.kappdev.wordbook.core.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kappdev.wordbook.core.domain.util.DialogState

@Composable
fun LoadingDialog(state: DialogState<Int?>) {
    if (state.isVisible.value) {
        LoadingDialog(state.dialogData.value)
    }
}

@Composable
fun LoadingDialog(messageRes: Int?) {
    val context = LocalContext.current
    LoadingDialog(messageRes?.let(context::getString))
}

@Composable
fun LoadingDialog(
    message: String? = null
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Box {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceTint, RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                CustomCircleLoader(
                    isVisible = true,
                    modifier = Modifier.size(32.dp),
                    color = MaterialTheme.colorScheme.primary
                )

                if (!message.isNullOrBlank()) {
                    AnimateDottedText(text = message)
                }
            }
        }
    }
}