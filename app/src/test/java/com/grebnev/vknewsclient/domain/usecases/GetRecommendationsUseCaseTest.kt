package com.grebnev.vknewsclient.domain.usecases

import com.grebnev.vknewsclient.core.wrappers.ErrorType
import com.grebnev.vknewsclient.core.wrappers.ResultStatus
import com.grebnev.vknewsclient.domain.entity.FeedPost
import com.grebnev.vknewsclient.domain.repository.RecommendationsFeedRepository
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

class GetRecommendationsUseCaseTest {
    @MockK
    private lateinit var repository: RecommendationsFeedRepository

    private lateinit var useCase: GetRecommendationsUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = GetRecommendationsUseCase(repository)
    }

    @Test
    fun `invoke should return recommendations from repository`() {
        val mockFlow = mockk<Flow<ResultStatus<List<FeedPost>, ErrorType>>>()
        every { repository.getRecommendations } returns mockFlow

        val result = useCase()

        assertEquals(mockFlow, result)
        verify { repository.getRecommendations }
    }

    @Test
    fun `close should call repository close`() {
        every { repository.close() } just Runs

        useCase.close()

        verify { repository.close() }
    }
}