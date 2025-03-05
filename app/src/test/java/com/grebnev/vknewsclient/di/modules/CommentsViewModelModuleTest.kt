package com.grebnev.vknewsclient.di.modules

import androidx.lifecycle.ViewModel
import com.grebnev.vknewsclient.presentation.comments.CommentsViewModel
import io.mockk.mockk
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CommentsViewModelModuleTest {
    @Test
    fun `bindCommentsViewModel should return viewModel instance`() {
        val mockCommentsViewModel = mockk<CommentsViewModel>()
        val commentsViewModelModule = object : CommentsViewModelModule {
            override fun bindCommentsViewModel(viewModel: CommentsViewModel): ViewModel {
                return viewModel
            }
        }

        val viewModel = commentsViewModelModule.bindCommentsViewModel(mockCommentsViewModel)

        assertNotNull(viewModel)
        assertTrue(viewModel is ViewModel)
    }
}