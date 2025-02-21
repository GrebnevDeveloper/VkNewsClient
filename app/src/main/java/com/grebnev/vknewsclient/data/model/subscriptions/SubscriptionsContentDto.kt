package com.grebnev.vknewsclient.data.model.subscriptions

import com.google.gson.annotations.SerializedName

data class SubscriptionsContentDto(
    @SerializedName("items") val listSubscriptions: List<SubscriptionsDto>
)
