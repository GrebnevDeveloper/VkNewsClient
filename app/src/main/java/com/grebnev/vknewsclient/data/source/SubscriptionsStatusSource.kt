package com.grebnev.vknewsclient.data.source

import com.grebnev.vknewsclient.core.handlers.ErrorHandler
import com.grebnev.vknewsclient.data.mapper.NewsFeedMapper
import com.grebnev.vknewsclient.data.network.ApiService
import com.grebnev.vknewsclient.di.scopes.ApplicationScope
import com.grebnev.vknewsclient.domain.entity.FeedPost
import com.grebnev.vknewsclient.domain.entity.Subscription
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@ApplicationScope
class SubscriptionsStatusSource @Inject constructor(
    private val apiService: ApiService,
    private val mapper: NewsFeedMapper,
    private val accessToken: AccessTokenSource
) {
    private val refreshSubscriptionsNeededEvents = MutableSharedFlow<Unit>(replay = 1)
    private val subscriptionState = loadSubscribes()
        .stateIn(
            scope = CoroutineScope(Dispatchers.Default),
            started = SharingStarted.Lazily,
            initialValue = Subscription()
        )

    fun getSubscriptionsState(): StateFlow<Subscription> = subscriptionState

    private fun loadSubscribes(): Flow<Subscription> = flow {
        refreshSubscriptionsNeededEvents.emit(Unit)
        refreshSubscriptionsNeededEvents.collect {
            val response = apiService.getListSubscriptions(
                token = accessToken.getAccessToken()
            )
            val subscription = mapper.mapResponseToSubscriptions(response)

            emit(subscription)
        }
    }.retry {
        delay(ErrorHandler.RETRY_TIMEOUT)
        true
    }

    suspend fun changeSubscriptionStatus(feedPost: FeedPost) {
        val currentSubscription = subscriptionState.value
        val newSourceIds = currentSubscription.sourceIds.toMutableSet()
        if (!feedPost.isSubscribed) {
            newSourceIds.add(feedPost.communityId)
        } else {
            newSourceIds.remove(feedPost.communityId)
        }

        if (newSourceIds.isNotEmpty()) {
            apiService.saveListSubscriptions(
                token = accessToken.getAccessToken(),
                listId = currentSubscription.id,
                title = currentSubscription.title,
                sourceIds = newSourceIds.joinToString()
            )
        } else {
            apiService.deleteListSubscriptions(
                token = accessToken.getAccessToken(),
                listId = currentSubscription.id
            )
        }

        refreshSubscriptionsNeededEvents.emit(Unit)
    }
}