package com.grebnev.vknewsclient.di.modules

import com.grebnev.vknewsclient.data.repository.CommentsPostRepositoryImpl
import com.grebnev.vknewsclient.data.repository.ProfileInfoRepositoryImpl
import com.grebnev.vknewsclient.data.repository.RecommendationsFeedRepositoryImpl
import com.grebnev.vknewsclient.data.repository.SubscriptionsFeedRepositoryImpl
import com.grebnev.vknewsclient.domain.repository.CommentsPostRepository
import com.grebnev.vknewsclient.domain.repository.NewsFeedRepository
import com.grebnev.vknewsclient.domain.repository.ProfileInfoRepository
import com.grebnev.vknewsclient.domain.repository.RecommendationsFeedRepository
import com.grebnev.vknewsclient.domain.repository.SubscriptionsFeedRepository
import io.mockk.mockk
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RepositoryModuleTest {
    private lateinit var repositoryModule: RepositoryModule

    @Before
    fun setUp() {
        repositoryModule =
            object : RepositoryModule {
                override fun bindRecommendationsFeedRepository(
                    impl: RecommendationsFeedRepositoryImpl,
                ): RecommendationsFeedRepository = impl

                override fun bindSubscriptionsFeedRepository(
                    impl: SubscriptionsFeedRepositoryImpl,
                ): SubscriptionsFeedRepository = impl

                override fun bindRecommendationsToNewsFeedRepository(
                    impl: RecommendationsFeedRepositoryImpl,
                ): NewsFeedRepository = impl

                override fun bindSubscriptionsToNewsFeedRepository(
                    impl: SubscriptionsFeedRepositoryImpl,
                ): NewsFeedRepository = impl

                override fun bindCommentsPostRepository(
                    impl: CommentsPostRepositoryImpl,
                ): CommentsPostRepository = impl

                override fun bindProfileInfoRepository(
                    impl: ProfileInfoRepositoryImpl,
                ): ProfileInfoRepository = impl
            }
    }

    @Test
    fun `bindRecommendationsFeedRepository should return RecommendationsFeedRepository instance`() {
        val mockRecommendationsRepoImpl = mockk<RecommendationsFeedRepositoryImpl>()

        val repository = repositoryModule.bindRecommendationsFeedRepository(mockRecommendationsRepoImpl)

        assertNotNull(repository)
        assertTrue(repository is RecommendationsFeedRepository)
    }

    @Test
    fun `bindSubscriptionsFeedRepository should return SubscriptionsFeedRepository instance`() {
        val mockSubscriptionsRepoImpl = mockk<SubscriptionsFeedRepositoryImpl>()

        val repository = repositoryModule.bindSubscriptionsFeedRepository(mockSubscriptionsRepoImpl)

        assertNotNull(repository)
        assertTrue(repository is SubscriptionsFeedRepository)
    }

    @Test
    fun `bindRecommendationsToNewsFeedRepository should return NewsFeedRepository instance`() {
        val mockRecommendationsRepoImpl = mockk<RecommendationsFeedRepositoryImpl>()

        val repository = repositoryModule.bindRecommendationsToNewsFeedRepository(mockRecommendationsRepoImpl)

        assertNotNull(repository)
        assertTrue(repository is NewsFeedRepository)
    }

    @Test
    fun `bindSubscriptionsToNewsFeedRepository should return NewsFeedRepository instance`() {
        val mockSubscriptionsRepoImpl = mockk<SubscriptionsFeedRepositoryImpl>()

        val repository = repositoryModule.bindSubscriptionsToNewsFeedRepository(mockSubscriptionsRepoImpl)

        assertNotNull(repository)
        assertTrue(repository is NewsFeedRepository)
    }

    @Test
    fun `bindCommentsPostRepository should return CommentsPostRepository instance`() {
        val mockCommentsPostRepoImpl = mockk<CommentsPostRepositoryImpl>()

        val repository = repositoryModule.bindCommentsPostRepository(mockCommentsPostRepoImpl)

        assertNotNull(repository)
        assertTrue(repository is CommentsPostRepository)
    }

    @Test
    fun `bindProfileInfoRepository should return ProfileInfoRepository instance`() {
        val mockProfileInfoRepoImpl = mockk<ProfileInfoRepositoryImpl>()

        val repository = repositoryModule.bindProfileInfoRepository(mockProfileInfoRepoImpl)

        assertNotNull(repository)
        assertTrue(repository is ProfileInfoRepository)
    }
}