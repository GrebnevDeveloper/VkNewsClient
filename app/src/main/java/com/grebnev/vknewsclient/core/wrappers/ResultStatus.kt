package com.grebnev.vknewsclient.core.wrappers

sealed class ResultStatus<out T, out E> {
    data class Success<out T>(
        val data: T,
        val nextDataLoading: Boolean = false,
    ) : ResultStatus<T, Nothing>()

    data class Error<out E>(
        val error: E,
    ) : ResultStatus<Nothing, E>()

    data object Empty : ResultStatus<Nothing, Nothing>()
}