package com.kappdev.wordbook.core.domain.util

import kotlinx.coroutines.flow.FlowCollector

sealed class ResultState<out R> {
    data class Success<out R>(val result: R): ResultState<R>()
    data class Failure(val exception: Exception): ResultState<Nothing>()
    data class Loading(val message: String? = null): ResultState<Nothing>()
}

suspend fun <T> FlowCollector<ResultState<T>>.emitSuccess(value: T) {
    emit(ResultState.Success(value))
}

suspend fun <T> FlowCollector<ResultState<T>>.emitFailure(exception: Exception) {
    emit(ResultState.Failure(exception))
}

suspend fun <T> FlowCollector<ResultState<T>>.emitLoading(message: String? = null) {
    emit(ResultState.Loading(message))
}

suspend fun <T> FlowCollector<ResultState<T>>.emitFailure(message: String) {
    emit(ResultState.Failure(Exception(message)))
}