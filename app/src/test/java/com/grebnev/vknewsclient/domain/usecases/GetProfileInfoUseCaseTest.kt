package com.grebnev.vknewsclient.domain.usecases

import com.grebnev.vknewsclient.core.wrappers.ErrorType
import com.grebnev.vknewsclient.core.wrappers.ResultStatus
import com.grebnev.vknewsclient.domain.entity.ProfileInfo
import com.grebnev.vknewsclient.domain.repository.ProfileInfoRepository
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.verify
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetProfileInfoUseCaseTest {
    @MockK
    private lateinit var repository: ProfileInfoRepository

    @MockK
    private lateinit var mockFlow: Flow<ResultStatus<ProfileInfo, ErrorType>>

    private lateinit var useCase: GetProfileInfoUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        every { repository.getProfileInfo } returns mockFlow
        useCase = GetProfileInfoUseCase(repository)
    }

    @Test
    fun `getProfileInfo should return flow from repository`() {
        val result = useCase.getProfileInfo

        assertEquals(mockFlow, result)
    }

    @Test
    fun `retry should call repository retry`() =
        runTest {
            coEvery { repository.retry() } just Runs

            useCase.retry()

            coVerify { repository.retry() }
        }

    @Test
    fun `close should call repository close`() {
        every { repository.close() } just Runs

        useCase.close()

        verify { repository.close() }
    }
}