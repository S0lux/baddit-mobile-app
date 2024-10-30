package com.example.baddit.data.remote

import com.example.baddit.data.dto.auth.EmailVerificationRequestBody
import com.example.baddit.data.dto.auth.LoginRequestBody
import com.example.baddit.data.dto.auth.RegisterRequestBody
import com.example.baddit.data.dto.posts.VotePostRequestBody
import com.example.baddit.domain.model.auth.GetMeResponseDTO
import com.example.baddit.domain.model.auth.GetOtherResponseDTO
import com.example.baddit.domain.model.auth.LoginResponseDTO
import com.example.baddit.domain.model.comment.CommentResponseDTO
import com.example.baddit.domain.model.community.Community
import com.example.baddit.domain.model.community.GetACommunityResponseDTO
import com.example.baddit.domain.model.community.GetCommunityListResponseDTO
import com.example.baddit.domain.model.posts.PostResponseDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface BadditAPI {
    @GET("/v1/posts")
    suspend fun getPosts(
        @Query("communityName") communityName: String? = null,
        @Query("authorName") authorName: String? = null,
        @Query("cursor") cursor: String? = null,
        @Query("postTitle") postTitle: String? = null
    ): Response<PostResponseDTO>

    @GET("/v1/posts")
    suspend fun getPost(@Query("postId") postId: String): Response<PostResponseDTO>

    @POST("/v1/posts/{postId}/votes")
    suspend fun votePost(
        @Path("postId") postId: String,
        @Body voteBody: VotePostRequestBody
    ): Response<Unit>

    @POST("/v1/auth/login")
    suspend fun login(@Body loginBody: LoginRequestBody): Response<LoginResponseDTO>

    @POST("/v1/auth/signup")
    suspend fun signup(@Body loginBody: RegisterRequestBody): Response<Unit>

    @POST("/v1/auth/verification")
    suspend fun verify(@Body tokenBody: EmailVerificationRequestBody): Response<Unit>

    @GET("/v1/users/me")
    suspend fun getMe(): Response<GetMeResponseDTO>

    @GET("v1/users/{username}")
    suspend fun getOther(@Path("username") username: String): Response<GetOtherResponseDTO>

    @GET("/v1/comments")
    suspend fun getComments(
        @Query("postId") postId: String?=null,
        @Query("commentId") commentId: String? = null,
        @Query("authorName") authorName: String? = null,
        @Query("cursor") cursor: String? = null
    ): Response<CommentResponseDTO>

    // community
    @GET("v1/communities")
    suspend fun getCommunities(): Response<GetCommunityListResponseDTO>

    @GET("v1/communities/{communityName}")
    suspend fun getCommunity(@Path("communityName") communityName: String): Response<GetACommunityResponseDTO>


}