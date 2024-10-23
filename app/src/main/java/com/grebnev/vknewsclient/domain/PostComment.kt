package com.grebnev.vknewsclient.domain

data class PostComment(
    val id: Int,
    val avatarResId: Int,
    val authorName: String,
    val commentText: String,
    val publicationDate: String
)
