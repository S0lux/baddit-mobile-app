package com.example.baddit.data.remote

import com.example.baddit.data.model.posts.PostDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PostAPI {

    @GET("/v1/posts")
    suspend fun getPosts(
        @Query("communityName") communityName: String?,
        @Query("authorName") authorName: String?,
        @Query("cursor") cursor: String?,
        @Query("postTitle") postTitle: String?): Response<PostDTO>

    @GET("/v1/posts")
    suspend fun getPost(@Query("postId") postId: String): Response<PostDTO>
}