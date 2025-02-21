package com.grebnev.vknewsclient.data.model.comments

import com.google.gson.annotations.SerializedName

data class CommentsDto(
    @SerializedName("count") val count: Int
)
