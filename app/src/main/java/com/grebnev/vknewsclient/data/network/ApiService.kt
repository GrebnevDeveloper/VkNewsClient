package com.grebnev.vknewsclient.data.network

import com.grebnev.vknewsclient.data.model.comments.CommentsResponseDto
import com.grebnev.vknewsclient.data.model.news.statistics.LikesCountResponseDto
import com.grebnev.vknewsclient.data.model.subscriptions.SubscriptionsIdDto
import com.grebnev.vknewsclient.data.model.subscriptions.SubscriptionsResponseDto
import com.grebnev.vknewsclient.data.model.news.posts.NewsFeedResponseDto
import com.grebnev.vknewsclient.data.model.profile.ProfileInfoResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("newsfeed.getRecommended?v=5.199")
    suspend fun loadRecommendations(
        @Query("access_token") token: String
    ): NewsFeedResponseDto

    @GET("newsfeed.getRecommended?v=5.199")
    suspend fun loadRecommendations(
        @Query("access_token") token: String,
        @Query("start_from") nextFrom: String
    ): NewsFeedResponseDto

    @GET("likes.add?v=5.199&type=post")
    suspend fun addLike(
        @Query("access_token") token: String,
        @Query("owner_id") ownerId: Long,
        @Query("item_id") postId: Long
    ): LikesCountResponseDto

    @GET("likes.delete?v=5.199&type=post")
    suspend fun deleteLike(
        @Query("access_token") token: String,
        @Query("owner_id") ownerId: Long,
        @Query("item_id") postId: Long
    ): LikesCountResponseDto

    @GET("newsfeed.ignoreItem?v=5.199&type=wall")
    suspend fun ignoreFeedPost(
        @Query("access_token") token: String,
        @Query("owner_id") ownerId: Long,
        @Query("item_id") postId: Long
    )

    @GET("wall.getComments?v=5.199&extended=1&fields=photo_100&count=100")
    suspend fun loadComments(
        @Query("access_token") token: String,
        @Query("owner_id") ownerId: Long,
        @Query("post_id") postId: Long
    ): CommentsResponseDto

    @GET("account.getProfileInfo?v=5.199")
    suspend fun loadProfileInfo(
        @Query("access_token") token: String
    ): ProfileInfoResponseDto

    @GET("newsfeed.saveList?v=5.199")
    suspend fun saveListSubscriptions(
        @Query("access_token") token: String,
        @Query("list_id") listId: Long,
        @Query("title") title: String,
        @Query("source_ids") sourceIds: String,
    ) : SubscriptionsIdDto

    @GET("newsfeed.getLists?v=5.199")
    suspend fun getListSubscriptions(
        @Query("access_token") token: String,
        @Query("extended") extended: Int = 1,
    ) : SubscriptionsResponseDto

    @GET("newsfeed.get?v=5.199")
    suspend fun loadSubscriptionPosts(
        @Query("access_token") token: String,
        @Query("source_ids") sourceIds: String
    ): NewsFeedResponseDto

    @GET("newsfeed.get?v=5.199")
    suspend fun loadSubscriptionPosts(
        @Query("access_token") token: String,
        @Query("source_ids") sourceIds: String,
        @Query("start_from") nextFrom: String
    ): NewsFeedResponseDto
}