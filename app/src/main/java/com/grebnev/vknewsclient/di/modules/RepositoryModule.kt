package com.grebnev.vknewsclient.di.modules

import com.grebnev.vknewsclient.data.repository.CommentsPostRepositoryImpl
import com.grebnev.vknewsclient.data.repository.ProfileInfoRepositoryImpl
import com.grebnev.vknewsclient.data.repository.RecommendationsFeedRepositoryImpl
import com.grebnev.vknewsclient.data.repository.SubscriptionsFeedRepositoryImpl
import com.grebnev.vknewsclient.di.keys.NewsFeedType
import com.grebnev.vknewsclient.di.keys.NewsFeedTypeKey
import com.grebnev.vknewsclient.di.scopes.ApplicationScope
import com.grebnev.vknewsclient.domain.repository.CommentsPostRepository
import com.grebnev.vknewsclient.domain.repository.NewsFeedRepository
import com.grebnev.vknewsclient.domain.repository.ProfileInfoRepository
import com.grebnev.vknewsclient.domain.repository.RecommendationsFeedRepository
import com.grebnev.vknewsclient.domain.repository.SubscriptionsFeedRepository
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface RepositoryModule {
    @ApplicationScope
    @Binds
    fun bindRecommendationsFeedRepository(impl: RecommendationsFeedRepositoryImpl): RecommendationsFeedRepository

    @ApplicationScope
    @Binds
    fun bindSubscriptionsFeedRepository(impl: SubscriptionsFeedRepositoryImpl): SubscriptionsFeedRepository

    @ApplicationScope
    @Binds
    @IntoMap
    @NewsFeedTypeKey(NewsFeedType.RECOMMENDATIONS)
    fun bindRecommendationsToNewsFeedRepository(impl: RecommendationsFeedRepositoryImpl): NewsFeedRepository

    @ApplicationScope
    @Binds
    @IntoMap
    @NewsFeedTypeKey(NewsFeedType.SUBSCRIPTIONS)
    fun bindSubscriptionsToNewsFeedRepository(impl: SubscriptionsFeedRepositoryImpl): NewsFeedRepository

    @ApplicationScope
    @Binds
    fun bindCommentsPostRepository(impl: CommentsPostRepositoryImpl): CommentsPostRepository

    @ApplicationScope
    @Binds
    fun bindProfileInfoRepository(impl: ProfileInfoRepositoryImpl): ProfileInfoRepository
}