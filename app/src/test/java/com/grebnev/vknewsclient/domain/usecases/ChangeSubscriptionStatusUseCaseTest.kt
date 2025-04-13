package com.grebnev.vknewsclient.domain.usecases

import com.grebnev.vknewsclient.di.keys.NewsFeedType
import com.grebnev.vknewsclient.domain.entity.FeedPost
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

class ChangeSubscriptionStatusUseCaseTest {
    @MockK
    private lateinit var subscriptionsRepository: NewsFeedRepository

    @MockK
    private lateinit var feedPost: FeedPost

    private lateinit var useCase: ChangeSubscriptionStatusUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        val repositories = mapOf(NewsFeedType.SUBSCRIPTIONS to subscriptionsRepository)
        useCase = ChangeSubscriptionStatusUseCase(repositories)
    }

    @Test
    fun `invoke should call changeSubscriptionStatus on correct repository`() =
        runTest {
            coEvery { subscriptionsRepository.changeSubscriptionStatus(feedPost) } just Runs

            useCase(feedPost, NewsFeedType.SUBSCRIPTIONS)

            coVerify { subscriptionsRepository.changeSubscriptionStatus(feedPost) }
        }

    @Test(expected = IllegalArgumentException::class)
    fun `invoke should throw for unknown repository type`() =
        runTest {
            useCase(feedPost, NewsFeedType.RECOMMENDATIONS)
        }
}