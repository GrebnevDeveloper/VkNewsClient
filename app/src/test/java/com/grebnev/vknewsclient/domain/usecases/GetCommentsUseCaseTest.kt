package com.grebnev.vknewsclient.domain.usecases

import com.grebnev.vknewsclient.core.wrappers.ErrorType
import com.grebnev.vknewsclient.core.wrappers.ResultStatus
import com.grebnev.vknewsclient.domain.entity.FeedPost
import com.grebnev.vknewsclient.domain.entity.PostComment
import com.grebnev.vknewsclient.domain.repository.CommentsPostRepository
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetCommentsUseCaseTest {
    @MockK
    private lateinit var repository: CommentsPostRepository

    @MockK
    private lateinit var feedPost: FeedPost

    private lateinit var useCase: GetCommentsUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = GetCommentsUseCase(repository)
    }

    @Test
    fun `getCommentsPost should return comments from repository`() {
        val mockComments = mockk<Flow<ResultStatus<List<PostComment>, ErrorType>>>()
        every { repository.getComments(feedPost) } returns mockComments

        val result = useCase.getCommentsPost(feedPost)

        assertEquals(mockComments, result)
        verify { repository.getComments(feedPost) }
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