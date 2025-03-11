package com.grebnev.vknewsclient.data.model.subscriptions

import com.google.gson.annotations.SerializedName

data class SubscriptionsDto(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("source_ids") val sourceIds: Set<Long>,
)