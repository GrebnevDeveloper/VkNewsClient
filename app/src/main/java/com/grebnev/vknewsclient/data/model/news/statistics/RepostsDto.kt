package com.grebnev.vknewsclient.data.model.news.statistics

import com.google.gson.annotations.SerializedName

data class RepostsDto(
    @SerializedName("count") val count: Int
)
