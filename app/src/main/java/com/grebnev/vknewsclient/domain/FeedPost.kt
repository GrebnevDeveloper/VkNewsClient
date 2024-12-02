package com.grebnev.vknewsclient.domain

data class FeedPost(
    val id: String,
    val communityName: String,
    val publicationDate: String,
    val communityImageUrl: String,
    val contentText: String,
    val contentImageUrl: String?,
    val statisticsList: List<StatisticItem>,
    val isFavorite: Boolean
)
