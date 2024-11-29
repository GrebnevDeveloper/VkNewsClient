package com.grebnev.vknewsclient.presentation.news

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grebnev.vknewsclient.data.mapper.NewsFeedMapper
import com.grebnev.vknewsclient.data.network.ApiFactory
import com.grebnev.vknewsclient.domain.FeedPost
import com.grebnev.vknewsclient.domain.StatisticItem
import com.vk.id.VKID
import kotlinx.coroutines.launch

class NewsFeedViewModel : ViewModel() {

    private val initialState = NewsFeedScreenState.Initial

    private val _screenState = MutableLiveData<NewsFeedScreenState>(initialState)
    val screenState: LiveData<NewsFeedScreenState> = _screenState

    private val mapper = NewsFeedMapper()

    init {
        loadRecommendations()
    }

    private fun loadRecommendations() {
        viewModelScope.launch {
            val token = VKID.instance.accessToken?.token ?: return@launch
            val response = ApiFactory.apiService.loadRecommendations(token)
            val feedPosts = mapper.mapResponseToFeedPost(response)
            _screenState.value = NewsFeedScreenState.Posts(feedPosts)
        }
    }

    fun updateCount(feedPost: FeedPost, item: StatisticItem) {
        val currentState = screenState.value
        if (currentState !is NewsFeedScreenState.Posts) return

        val newFeedPosts = currentState.posts.toMutableList().apply {
            replaceAll { oldFeedPost ->
                if (oldFeedPost == feedPost) {
                    val oldStatistics = oldFeedPost.statisticsList
                    val newStatistics = oldStatistics.toMutableList().apply {
                        replaceAll { oldItem ->
                            if (item.type == oldItem.type) {
                                oldItem.copy(count = oldItem.count + 1)
                            } else {
                                oldItem
                            }
                        }
                    }
                    oldFeedPost.copy(statisticsList = newStatistics)
                } else {
                    oldFeedPost
                }
            }
        }
        _screenState.value = NewsFeedScreenState.Posts(newFeedPosts)
    }

    fun delete(feedPost: FeedPost) {
        val currentState = screenState.value
        if (currentState !is NewsFeedScreenState.Posts) return

        val modifiedFeedPostList = currentState.posts.toMutableList()
        modifiedFeedPostList.remove(feedPost)
        _screenState.value = NewsFeedScreenState.Posts(posts = modifiedFeedPostList)
    }
}