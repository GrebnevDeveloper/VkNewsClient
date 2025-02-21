package com.grebnev.vknewsclient.di.keys

import dagger.MapKey

@MapKey
@Retention(AnnotationRetention.RUNTIME)
annotation class NewsFeedTypeKey(val value: NewsFeedType)

enum class NewsFeedType {
    RECOMMENDATIONS,
    SUBSCRIPTIONS
}