package com.grebnev.vknewsclient.data.model.news.posts

import com.google.gson.annotations.SerializedName

data class PhotoUrlDto(
    @SerializedName("url") val photoUrl: String,
)