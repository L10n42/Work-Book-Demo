package com.kappdev.wordbook.core.presentation.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.kappdev.wordbook.R

@Composable
fun UnsavedChangesSheet(
    onDismiss: () -> Unit,
    onDiscard: () -> Unit,
    onSave: () -> Unit
) {
    AlertSheet(
        title = stringResource(R.string.unsaved_changes),
        message = stringResource(R.string.unsaved_changes_msg),
        positive = stringResource(R.string.save),
        negative = stringResource(R.string.discard),
        onDismiss = onDismiss,
        onNegative = onDiscard,
        onPositive = onSave
    )
}