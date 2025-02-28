package com.grebnev.vknewsclient.data

import com.grebnev.vknewsclient.domain.state.ErrorType
import timber.log.Timber
import java.io.IOException

interface ErrorHandlingValues {
    companion object {
        const val RETRY_TIMEOUT = 3000L
        const val MAX_COUNT_RETRY = 3L

        fun getTypeError(throwable: Throwable): ErrorType {
            return when(throwable) {
                is IOException -> {
                    Timber.e("Network exception")
                    ErrorType.NETWORK_ERROR
                }
                else -> {
                    Timber.e("Unknown error")
                    ErrorType.UNKNOWN_ERROR
                }
            }
        }
    }
}