package com.kappdev.wordbook.core.domain.util

import androidx.annotation.StringRes

sealed class Result<out R> {
    data class Success<out R>(val value: R): Result<R>()
    data class Failure(@StringRes val messageResId: Int): Result<Nothing>()

    companion object
}