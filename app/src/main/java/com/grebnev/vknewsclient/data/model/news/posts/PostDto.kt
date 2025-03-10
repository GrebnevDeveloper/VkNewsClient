package com.grebnev.vknewsclient.data.model.news.posts

import com.google.gson.annotations.SerializedName
import com.grebnev.vknewsclient.data.model.comments.CommentsDto
import com.grebnev.vknewsclient.data.model.news.statistics.LikesDto
import com.grebnev.vknewsclient.data.model.news.statistics.RepostsDto
import com.grebnev.vknewsclient.data.model.news.statistics.ViewsDto

data class PostDto(
    @SerializedName("id") val id: Long,
    @SerializedName("source_id") val communityId: Long,
    @SerializedName("text") val text: String,
    @SerializedName("date") val date: Long,
    @SerializedName("likes") val likes: LikesDto?,
    @SerializedName("views") val views: ViewsDto?,
    @SerializedName("comments") val comments: CommentsDto?,
    @SerializedName("reposts") val reposts: RepostsDto?,
    @SerializedName("attachments") val attachments: List<AttachmentDto>?,
)