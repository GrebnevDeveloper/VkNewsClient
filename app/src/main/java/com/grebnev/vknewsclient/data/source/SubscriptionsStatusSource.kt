package com.grebnev.vknewsclient.data.source

import com.grebnev.vknewsclient.data.mapper.NewsFeedMapper
import com.grebnev.vknewsclient.data.network.ApiService
import com.grebnev.vknewsclient.di.scopes.ApplicationScope
import com.grebnev.vknewsclient.domain.entity.FeedPost
import com.grebnev.vknewsclient.domain.entity.Subscription
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@ApplicationScope
class SubscriptionsStatusSource @Inject constructor(
    private val apiService: ApiService,
    private val mapper: NewsFeedMapper,
    private val accessToken: AccessTokenSource
) {

    private val subscriptionState = MutableStateFlow(Subscription())

    fun getSubscriptionsState(): StateFlow<Subscription> = subscriptionState

    fun loadSubscribes(): Flow<Subscription> = flow {
        val response = apiService.getListSubscriptions(
            token = accessToken.getAccessToken()
        )
        val subscription = mapper.mapResponseToSubscriptions(response)

        subscriptionState.value = subscription

        emit(subscriptionState.value)
    }

    suspend fun changeSubscriptionStatus(feedPost: FeedPost) {
        val currentSubscription = subscriptionState.value
        val newSourceIds = currentSubscription.sourceIds.toMutableSet()
        if (!feedPost.isSubscribed) {
            newSourceIds.add(feedPost.communityId)
        } else {
            newSourceIds.remove(feedPost.communityId)
        }

        val responseId = if (newSourceIds.isNotEmpty()) {
            val response = apiService.saveListSubscriptions(
                token = accessToken.getAccessToken(),
                listId = currentSubscription.id,
                title = currentSubscription.title,
                sourceIds = newSourceIds.joinToString()
            )
            response.id
        } else {
            apiService.deleteListSubscriptions(
                token = accessToken.getAccessToken(),
                listId = currentSubscription.id
            )
            currentSubscription.id
        }

        subscriptionState.value = currentSubscription.copy(
            id = responseId,
            sourceIds = newSourceIds
        )
    }
}