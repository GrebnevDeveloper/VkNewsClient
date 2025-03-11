package com.grebnev.vknewsclient.di.components

import com.grebnev.vknewsclient.domain.entity.FeedPost
import com.grebnev.vknewsclient.presentation.base.ViewModelFactory
import io.mockk.mockk
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CommentsComponentTest {
    @Test
    fun `getViewModuleFactory should return instance of viewModelFactory`() {
        val mockFeedPost = mockk<FeedPost>()
        val component =
            DaggerApplicationComponent
                .builder()
                .appModule(mockk())
                .networkModule(mockk())
                .build()
                .getCommentsComponentFactory()
                .create(mockFeedPost)

        val viewModelFactory = component.getViewModuleFactory()

        assertNotNull(viewModelFactory)
        assertTrue(viewModelFactory is ViewModelFactory)
    }
}