package com.grebnev.vknewsclient.presentation.news

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.grebnev.vknewsclient.domain.FeedPost
import com.grebnev.vknewsclient.domain.StatisticItem

class NewsFeedViewModel : ViewModel() {
    private val initialFeedPostList = mutableListOf<FeedPost>().apply {
        repeat(500) {
            add(
                FeedPost(it)
            )
        }
    }

    private val initialState = NewsFeedScreenState.Posts(initialFeedPostList)

    private val _screenState = MutableLiveData<NewsFeedScreenState>(initialState)
    val screenState: LiveData<NewsFeedScreenState> = _screenState


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