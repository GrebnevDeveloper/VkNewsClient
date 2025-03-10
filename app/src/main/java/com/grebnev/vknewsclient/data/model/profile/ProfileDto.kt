package com.grebnev.vknewsclient.data.model.profile

import com.google.gson.annotations.SerializedName

data class ProfileDto(
    @SerializedName("id") val id: Long,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("photo_100") val authorAvatarUrl: String,
)