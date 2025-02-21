package com.grebnev.vknewsclient.domain.entity

data class Subscription(
    val id: Long = INITIAL_ID,
    val title: String = UNIQUE_TITLE,
    val sourceIds: Set<Long> = setOf()
) {
    companion object {
        private const val INITIAL_ID = 1001L
        const val UNIQUE_TITLE = "SubscriptionsVkNews"
    }
}
