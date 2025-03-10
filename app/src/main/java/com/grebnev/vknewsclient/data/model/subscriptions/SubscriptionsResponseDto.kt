package com.grebnev.vknewsclient.data.model.subscriptions

import com.google.gson.annotations.SerializedName

data class SubscriptionsResponseDto(
    @SerializedName("response") val listSubscriptionContent: SubscriptionsContentDto,
)