package com.grebnev.vknewsclient.data.model.news.statistics

import com.google.gson.annotations.SerializedName

data class LikesCountResponseDto(
    @SerializedName("response") val likes: LikesCountDto,
)