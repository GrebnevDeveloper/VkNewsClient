package com.grebnev.vknewsclient.di.modules

import androidx.lifecycle.ViewModel
import com.grebnev.vknewsclient.presentation.main.MainViewModel
import com.grebnev.vknewsclient.presentation.news.recommendations.RecommendationsFeedViewModel
import com.grebnev.vknewsclient.presentation.news.subscriptions.SubscriptionsFeedViewModel
import com.grebnev.vknewsclient.presentation.profile.ProfileInfoViewModel
import io.mockk.mockk
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ViewModelModuleTest {
    private lateinit var viewModelModule: ViewModelModule

    @Before
    fun setUp() {
        viewModelModule =
            object : ViewModelModule {
                override fun bindNewsFeedViewModel(viewModel: RecommendationsFeedViewModel): ViewModel =
                    viewModel

                override fun bindSubscriptionsFeedViewModel(
                    viewModel: SubscriptionsFeedViewModel,
                ): ViewModel = viewModel

                override fun bindMainViewModel(viewModel: MainViewModel): ViewModel = viewModel

                override fun bindProfileInfoViewModel(viewModel: ProfileInfoViewModel): ViewModel = viewModel
            }
    }

    @Test
    fun `bindNewsFeedViewModel should return ViewModel instance`() {
        val mockRecommendationsViewModel = mockk<RecommendationsFeedViewModel>()

        val viewModel = viewModelModule.bindNewsFeedViewModel(mockRecommendationsViewModel)

        assertNotNull(viewModel)
        assertTrue(viewModel is ViewModel)
    }

    @Test
    fun `bindSubscriptionsFeedViewModel should return ViewModel instance`() {
        val mockSubscriptionsViewModel = mockk<SubscriptionsFeedViewModel>()

        val viewModel = viewModelModule.bindSubscriptionsFeedViewModel(mockSubscriptionsViewModel)

        assertNotNull(viewModel)
        assertTrue(viewModel is ViewModel)
    }

    @Test
    fun `bindMainViewModel should return ViewModel instance`() {
        val mockMainViewModel = mockk<MainViewModel>()

        val viewModel = viewModelModule.bindMainViewModel(mockMainViewModel)

        assertNotNull(viewModel)
        assertTrue(viewModel is ViewModel)
    }

    @Test
    fun `bindProfileInfoViewModel should return ViewModel instance`() {
        val mockProfileInfoViewModel = mockk<ProfileInfoViewModel>()

        val viewModel = viewModelModule.bindProfileInfoViewModel(mockProfileInfoViewModel)

        assertNotNull(viewModel)
        assertTrue(viewModel is ViewModel)
    }
}