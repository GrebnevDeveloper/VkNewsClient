package com.grebnev.vknewsclient.data.model

import com.google.gson.annotations.SerializedName

data class ProfileInfoResponseDto(
    @SerializedName("response") val profileInfo: ProfileInfoDto
)