package com.grebnev.vknewsclient.domain.usecases

import com.grebnev.vknewsclient.di.keys.NewsFeedType
import com.grebnev.vknewsclient.domain.repository.NewsFeedRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.StateFlow
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class HasNextDataLoadingUseCaseTest {
    @MockK
    private lateinit var subscriptionsRepository: NewsFeedRepository

    private lateinit var useCase: HasNextDataLoadingUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        val repositories = mapOf(NewsFeedType.SUBSCRIPTIONS to subscriptionsRepository)
        useCase = HasNextDataLoadingUseCase(repositories)
    }

    @Test
    fun `invoke should return hasNextDataLoading from correct repository`() {
        val mockStateFlow = mockk<StateFlow<Boolean>>()
        every { subscriptionsRepository.hasNextDataLoading() } returns mockStateFlow

        val result = useCase(NewsFeedType.SUBSCRIPTIONS)

        assertEquals(mockStateFlow, result)
        verify { subscriptionsRepository.hasNextDataLoading() }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `invoke should throw for unknown repository type`() {
        useCase(NewsFeedType.RECOMMENDATIONS)
    }
}