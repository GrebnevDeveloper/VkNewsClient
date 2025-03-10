package com.grebnev.vknewsclient.data.model.comments

import com.google.gson.annotations.SerializedName
import com.grebnev.vknewsclient.data.model.profile.ProfileDto

data class CommentsContentDto(
    @SerializedName("items") val comments: List<CommentDto>,
    @SerializedName("profiles") val profiles: List<ProfileDto>,
)