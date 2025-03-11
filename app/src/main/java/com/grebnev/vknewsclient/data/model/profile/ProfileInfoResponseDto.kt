package com.grebnev.vknewsclient.data.model.profile

import com.google.gson.annotations.SerializedName

data class ProfileInfoResponseDto(
    @SerializedName("response") val profileInfo: ProfileInfoDto,
)