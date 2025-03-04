package com.grebnev.vknewsclient.presentation.base

import android.content.Context
import com.grebnev.vknewsclient.R
import com.grebnev.vknewsclient.core.wrappers.ErrorType
import javax.inject.Inject

class ErrorMessageProvider @Inject constructor(
    private val context: Context
) {
    fun getErrorMessage(type: ErrorType): String {
        return when (type) {
            ErrorType.NETWORK_ERROR ->
                context.getString(R.string.network_error_message)

            ErrorType.UNKNOWN_ERROR ->
                context.getString(R.string.unknown_error_message)
        }
    }
}