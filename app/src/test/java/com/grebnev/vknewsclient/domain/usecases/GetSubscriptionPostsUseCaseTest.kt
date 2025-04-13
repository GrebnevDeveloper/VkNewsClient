package com.grebnev.vknewsclient.domain.usecases

import com.grebnev.vknewsclient.core.wrappers.ErrorType
import com.grebnev.vknewsclient.core.wrappers.ResultStatus
import com.grebnev.vknewsclient.domain.entity.FeedPost
import com.grebnev.vknewsclient.domain.repository.SubscriptionsFeedRepository
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.Flow
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetSubscriptionPostsUseCaseTest {
    @MockK
    private lateinit var repository: SubscriptionsFeedRepository

    private lateinit var useCase: GetSubscriptionPostsUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = GetSubscriptionPostsUseCase(repository)
    }

    @Test
    fun `invoke should return subscription posts from repository`() {
        val mockFlow = mockk<Flow<ResultStatus<List<FeedPost>, ErrorType>>>()
        every { repository.getSubscriptionPosts } returns mockFlow

        val result = useCase()

        assertEquals(mockFlow, result)
        verify { repository.getSubscriptionPosts }
    }

    @Test
    fun `close should call repository close`() {
        every { repository.close() } just Runs

        useCase.close()

        verify { repository.close() }
    }
}