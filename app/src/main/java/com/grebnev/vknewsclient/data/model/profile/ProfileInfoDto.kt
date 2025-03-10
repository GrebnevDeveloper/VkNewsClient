package com.grebnev.vknewsclient.data.model.profile

import com.google.gson.annotations.SerializedName

data class ProfileInfoDto(
    @SerializedName("id") val id: Long,
    @SerializedName("photo_200") val avatarUrl: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
)