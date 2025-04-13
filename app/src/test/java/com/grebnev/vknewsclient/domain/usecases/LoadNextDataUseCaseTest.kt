package com.grebnev.vknewsclient.domain.usecases

import com.grebnev.vknewsclient.di.keys.NewsFeedType
import com.grebnev.vknewsclient.domain.repository.NewsFeedRepository
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.just
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class LoadNextDataUseCaseTest {
    @MockK
    private lateinit var subscriptionsRepository: NewsFeedRepository

    private lateinit var useCase: LoadNextDataUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        val repositories = mapOf(NewsFeedType.SUBSCRIPTIONS to subscriptionsRepository)
        useCase = LoadNextDataUseCase(repositories)
    }

    @Test
    fun `invoke should call loadNextData on correct repository`() =
        runTest {
            coEvery { subscriptionsRepository.loadNextData() } just Runs

            useCase(NewsFeedType.SUBSCRIPTIONS)

            coVerify { subscriptionsRepository.loadNextData() }
        }

    @Test(expected = IllegalArgumentException::class)
    fun `invoke should throw for unknown repository type`() =
        runTest {
            useCase(NewsFeedType.RECOMMENDATIONS)
        }
}