package com.grebnev.vknewsclient.core

import com.grebnev.vknewsclient.core.handlers.ErrorHandler
import com.grebnev.vknewsclient.core.wrappers.ErrorType
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.IOException

class ErrorHandlerTest {

    @Test
    fun `getErrorType should return NETWORK_ERROR for IOException`() {
        val mockIOException: Throwable = mockk<IOException>()

        val result = ErrorHandler.getErrorType(mockIOException)

        assertEquals(ErrorType.NETWORK_ERROR, result)
    }

    @Test
    fun `getErrorType should return UNKNOWN_ERROR for other exceptions`() {
        val mockThrowable: Throwable = mockk()

        val result = ErrorHandler.getErrorType(mockThrowable)

        assertEquals(ErrorType.UNKNOWN_ERROR, result)
    }
}