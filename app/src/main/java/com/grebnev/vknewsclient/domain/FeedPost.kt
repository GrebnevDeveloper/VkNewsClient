package com.grebnev.vknewsclient.domain

import com.grebnev.vknewsclient.R

data class FeedPost(
    val communityName: String = "dev/null",
    val publicationDate: String = "14:00",
    val avatarResId: Int = R.drawable.post_comunity_thumbnail,
    val contentText: String = "Lorem ipsum dolor sit amet? consectetur adipiscing elit.",
    val contentImageResId: Int = R.drawable.post_content_image,
    val statisticsList: List<StatisticItem> = listOf(
        StatisticItem(StatisticType.VIEWS, 966),
        StatisticItem(StatisticType.SHARES, 7),
        StatisticItem(StatisticType.COMMENTS, 13),
        StatisticItem(StatisticType.LIKES, 29),
    )
)
