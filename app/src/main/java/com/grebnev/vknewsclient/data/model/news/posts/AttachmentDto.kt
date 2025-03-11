package com.grebnev.vknewsclient.data.model.news.posts

import com.google.gson.annotations.SerializedName

data class AttachmentDto(
    @SerializedName("photo") val photo: PhotoDto?,
)