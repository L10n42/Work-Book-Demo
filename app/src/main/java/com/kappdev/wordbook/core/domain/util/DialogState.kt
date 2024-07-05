package com.kappdev.wordbook.core.domain.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

sealed interface DialogState<T> {
    val dialogData: State<T>
    val isVisible: State<Boolean>
}

sealed interface MutableDialogState<T> : DialogState<T> {
    override val dialogData: State<T>
    override val isVisible: State<Boolean>

    fun showDialog(data: T)
    fun showDialog()
    fun hideDialog()
}

private class MutableDialogStateImpl<T>(initialData: T) : MutableDialogState<T> {

    private var _dialogData = mutableStateOf(initialData)

    override val dialogData: State<T>
        get() = _dialogData

    private var _isVisible = mutableStateOf(false)

    override val isVisible: State<Boolean>
        get() = _isVisible

    override fun showDialog() {
        _isVisible.value = true
    }

    override fun showDialog(data: T) {
        _dialogData.value = data
        _isVisible.value = true
    }

    override fun hideDialog() {
        _isVisible.value = false
    }
}

fun <T> mutableDialogStateOf(initialData: T): MutableDialogState<T> {
    return MutableDialogStateImpl(initialData)
}

@Composable
fun <T> rememberMutableDialogState(initialData: T) = remember {
    mutableDialogStateOf(initialData)
}