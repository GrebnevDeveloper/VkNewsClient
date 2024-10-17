package com.grebnev.vknewsclient

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.grebnev.vknewsclient.domain.FeedPost
import com.grebnev.vknewsclient.domain.StatisticItem

class VkNewsMainScreenViewModel : ViewModel() {
    private val _feedPost = MutableLiveData(FeedPost())
    val feedPost: LiveData<FeedPost> = _feedPost

    fun updateCount(item: StatisticItem) {
        val oldStatistics = feedPost.value?.statisticsList
            ?: throw IllegalStateException("Value from feedPost is null")
        val newStatistics = oldStatistics.toMutableList().apply {
            replaceAll { oldItem ->
                if (item.type == oldItem.type) {
                    oldItem.copy(count = oldItem.count + 1)
                } else {
                    oldItem
                }
            }
        }
        _feedPost.value = feedPost.value?.copy(statisticsList = newStatistics)
    }
}