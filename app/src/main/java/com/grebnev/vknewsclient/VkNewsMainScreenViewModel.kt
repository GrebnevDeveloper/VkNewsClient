package com.grebnev.vknewsclient

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.grebnev.vknewsclient.domain.FeedPost
import com.grebnev.vknewsclient.domain.StatisticItem

class VkNewsMainScreenViewModel : ViewModel() {
    private val initialFeedPostList = mutableListOf<FeedPost>().apply {
        repeat(500) {
            add(
                FeedPost(it)
            )
        }
    }

    private val _feedPostList = MutableLiveData<List<FeedPost>>(initialFeedPostList)
    val feedPostList: LiveData<List<FeedPost>> = _feedPostList

    fun updateCount(feedPost: FeedPost, item: StatisticItem) {
        _feedPostList.value = feedPostList.value?.toMutableList()?.apply {
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
        } ?: throw IllegalStateException("feedPostList is null")
    }

    fun delete(feedPost: FeedPost) {
        val modifiedFeedPostList = _feedPostList.value?.toMutableList() ?: mutableListOf()
        modifiedFeedPostList.remove(feedPost)
        _feedPostList.value = modifiedFeedPostList
    }
}