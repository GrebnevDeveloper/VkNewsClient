package com.grebnev.vknewsclient.data.model.news.statistics

import com.google.gson.annotations.SerializedName

data class LikesCountDto(
    @SerializedName("likes") val count: Int,
)