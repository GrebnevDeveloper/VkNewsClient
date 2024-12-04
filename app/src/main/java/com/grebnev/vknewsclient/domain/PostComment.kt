package com.grebnev.vknewsclient.domain

data class PostComment(
    val id: Long,
    val authorAvatarUrl: String,
    val authorName: String,
    val commentText: String,
    val publicationDate: String
)
