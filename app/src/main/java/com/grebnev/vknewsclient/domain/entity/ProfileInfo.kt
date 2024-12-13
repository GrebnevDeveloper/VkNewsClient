package com.grebnev.vknewsclient.domain.entity

import androidx.compose.runtime.Immutable

@Immutable
data class ProfileInfo(
    val id: Long,
    val avatarUrl: String,
    val firstName: String,
    val lastName: String
)